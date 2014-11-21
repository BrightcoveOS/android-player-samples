package com.brightcove.samples.android.closedcaptioning.dfxp;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;

import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
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

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}