package com.brightcove.samples.android.closedcaptioning.dfxp;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;

import com.brightcove.player.captioning.BrightcoveClosedCaptioningTextView;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.TTMLDocument;
import com.brightcove.player.view.BrightcoveClosedCaptioningView;
import com.brightcove.player.view.BrightcoveVideoView;
import com.robotium.solo.Solo;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * White box style tests for DFXP/TTML closed captioning in the Brightcove SDK.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private final String TAG = this.getClass().getSimpleName();

    private Solo solo;
    private BrightcoveVideoView brightcoveVideoView;
    private EventEmitter eventEmitter;
    private MainActivity mainActivity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        mainActivity = getActivity();
        solo = new Solo(getInstrumentation(), mainActivity);
        brightcoveVideoView = (BrightcoveVideoView) mainActivity.findViewById(R.id.brightcove_video_view);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        eventEmitter.once(EventType.DID_SET_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                brightcoveVideoView.start();
            }
        });
    }

    /**
     * Verify that upon receiving DFXP captions that a single BrightcoveClosedCaptioningView is
     * added to the BrightcoveVideoView.
     */
    public void testCaptionViewAdded() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Log.v(TAG, "testCaptionViewAdded:");

        eventEmitter.once(EventType.DID_LOAD_CAPTIONS, new EventListener() {
            @Override
            public void processEvent(Event event) {
                View parent = solo.getView(R.id.brightcove_video_view);
                ArrayList<BrightcoveClosedCaptioningView> captioningViews = solo.getCurrentViews(BrightcoveClosedCaptioningView.class, parent);
                assertTrue("A BrightcoveClosedCaptioningView was added.", captioningViews.size() == 1);
                latch.countDown();
            }
        });
        assertTrue("Timeout occurred.", latch.await(2, TimeUnit.MINUTES));
    }

    /**
     * Verify that the number of DFXP captions parsed from the DFXP/TTML file is equal to the number
     * of rendered DFXP captions.
     */
    public void testNumberDFXPCaptionsRendered() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Log.v(TAG, "testNumberDFXPCaptionsRendered:");

        eventEmitter.once(EventType.DID_LOAD_CAPTIONS, new EventListener() {
            @Override
            public void processEvent(Event event) {
                int numDFXPCaptions = getNumberDFXPCaptions(event);
                View parent = solo.getView(R.id.brightcove_video_view);
                ArrayList<BrightcoveClosedCaptioningView> captioningViews = solo.getCurrentViews(BrightcoveClosedCaptioningView.class, parent);
                if (captioningViews.size() == 1) {
                    ArrayList<BrightcoveClosedCaptioningTextView> captioningTextViews = solo.getCurrentViews(BrightcoveClosedCaptioningTextView.class, captioningViews.get(0));
                    assertTrue("All DFXP captions received have a rendered view.", captioningTextViews.size() == numDFXPCaptions);
                    latch.countDown();
                }
            }
        });
        assertTrue("Timeout occurred.", latch.await(2, TimeUnit.MINUTES));

    }

    /**
     * Verify that captions are invisible when they are disabled via SharedPreferences.
     */
    public void testCaptionsInvisibleWhenDisabled() throws InterruptedException {
        Log.v(TAG, "testCaptionsInvisibleWhenDisabled:");

        solo.clickOnActionBarItem(R.id.action_cc_settings);
        solo.clickOnRadioButton(1);

        assertFalse("Captions are currently hidden.", areCaptionsVisible());
    }

    /**
     * Verify that captions are visible when they are enabled via SharedPreferences.
     */
    public void testCaptionsVisibleWhenEnabled() throws InterruptedException {
        Log.v(TAG, "testCaptionsVisibleWhenEnabled:");

        solo.clickOnActionBarItem(R.id.action_cc_settings);
        solo.clickOnRadioButton(0);

        assertTrue("Captions are currently visible.", areCaptionsVisible());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    private int getNumberDFXPCaptions(Event event) {
        TTMLDocument document = (TTMLDocument) event.properties.get(Event.TTML_DOCUMENT);
        return document.getCaptions().size();
    }

    private boolean isCaptioningViewAttached() {
        View parent = solo.getView(R.id.brightcove_video_view);
        ArrayList<BrightcoveClosedCaptioningView> captioningViews = solo.getCurrentViews(BrightcoveClosedCaptioningView.class, parent);
        if (captioningViews.size() == 1) {
            return true;
        } else {
            return false;
        }
    }

    private boolean areCaptionsVisible() {
        int numVisibile = 0;
        int numInvisible = 0;

        View parent = solo.getView(R.id.brightcove_video_view);
        ArrayList<BrightcoveClosedCaptioningView> captioningViews = solo.getCurrentViews(BrightcoveClosedCaptioningView.class, parent);
        if (captioningViews.size() == 1) {
            ArrayList<BrightcoveClosedCaptioningTextView> captioningTextViews = solo.getCurrentViews(BrightcoveClosedCaptioningTextView.class, captioningViews.get(0));
            for (BrightcoveClosedCaptioningTextView textView : captioningTextViews) {
                if (textView.isShown()) {
                    numVisibile++;
                } else {
                    numInvisible++;
                }
            }
        }
        if (numVisibile > 0 && numInvisible == 0) {
            return true;
        } else if (numInvisible > 0 && numVisibile == 0) {
            return false;
        } else {
            return false;
        }
    }
}