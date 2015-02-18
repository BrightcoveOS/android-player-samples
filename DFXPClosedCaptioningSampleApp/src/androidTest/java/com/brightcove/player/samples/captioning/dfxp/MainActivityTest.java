package com.brightcove.player.samples.captioning.dfxp;

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
import com.brightcove.samples.android.closedcaptioning.dfxp.MainActivity;
import com.brightcove.samples.android.closedcaptioning.dfxp.R;
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
                assertEquals("A BrightcoveClosedCaptioningView was supposed to be added, but wasn't.", captioningViews.size(), 1);
                latch.countDown();
            }
        });
        assertTrue("Timeout occurred.", latch.await(1, TimeUnit.MINUTES));
    }

    /**
     * Verify that the number of DFXP captions parsed from the DFXP/TTML file is equal to the number
     * of rendered DFXP captions.
     */
    public void testNumberDFXPCaptionsRendered() throws InterruptedException {
        Log.v(TAG, "testNumberDFXPCaptionsRendered:");

        final int[] numDFXPCaptions = new int[1];

        eventEmitter.once(EventType.DID_LOAD_CAPTIONS, new EventListener() {
            @Override
            public void processEvent(Event event) {
                numDFXPCaptions[0] = getNumberDFXPCaptions(event);
            }
        });

        View parent = solo.getView(R.id.brightcove_video_view);
        solo.sleep(3500);
        ArrayList<BrightcoveClosedCaptioningView> captioningViews = solo.getCurrentViews(BrightcoveClosedCaptioningView.class,  parent);
        if (captioningViews.size() == 1) {
            ArrayList<BrightcoveClosedCaptioningTextView> captioningTextViews = solo.getCurrentViews(BrightcoveClosedCaptioningTextView.class, captioningViews.get(0));
            assertEquals("Not all DFXP captions have a rendered view.", captioningTextViews.size(), numDFXPCaptions[0]);
        }
    }

    /**
     * Verify that captions are visible when they are enabled via SharedPreferences.
     */
    public void testCaptionsVisibleWhenEnabled() throws InterruptedException {
        Log.v(TAG, "testCaptionsVisibleWhenEnabled:");
        solo.clickOnActionBarItem(R.id.action_cc_settings);
        solo.clickOnRadioButton(0);
        solo.sleep(1000);
        assertTrue("Captions are supposed to currently be visible.", areCaptionsVisible());
    }

    /**
     * Verify that captions are invisible when they are disabled via SharedPreferences.
     */
    public void testCaptionsInvisibleWhenDisabled() throws InterruptedException {
        Log.v(TAG, "testCaptionsInvisibleWhenDisabled:");
        solo.clickOnActionBarItem(R.id.action_cc_settings);
        solo.clickOnRadioButton(1);
        solo.sleep(1000);
        assertFalse("Captions are supposed to currently be hidden.", areCaptionsVisible());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /**
     * Helper method to get the number of DFXP captions from the SDK.
     * @param event - The event containing the DFXP document.
     * @return the number of DFXP captions.
     */
    private int getNumberDFXPCaptions(Event event) {
        TTMLDocument document = (TTMLDocument) event.properties.get(Event.TTML_DOCUMENT);
        return document.getCaptions().size();
    }

    /**
     * Helper method to check if a caption textview is visible.
     * @return true if visible, false otherwise.
     */
    private boolean areCaptionsVisible() {
        View parent = solo.getView(R.id.brightcove_video_view);
        ArrayList<BrightcoveClosedCaptioningView> captioningViews = solo.getCurrentViews(BrightcoveClosedCaptioningView.class, parent);
        if (captioningViews.size() == 1) {
            ArrayList<BrightcoveClosedCaptioningTextView> captioningTextViews = solo.getCurrentViews(BrightcoveClosedCaptioningTextView.class, captioningViews.get(0));
            for (BrightcoveClosedCaptioningTextView textView : captioningTextViews) {
                if (textView.isShown()) {
                    return true;
                }
            }
        }
        return false;
    }
}