package com.brightcove.samples.android.closedcaptioning.dfxp;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.view.BrightcoveVideoView;
import com.robotium.solo.Solo;

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
     * Test
     */
    public void testCaptionsLoaded() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Log.v(TAG, "testCaptionsLoaded");
        eventEmitter.once(EventType.DID_LOAD_CAPTIONS, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.v(TAG, "DERP CAPS LOADED");
                latch.countDown();
            }
        });
        assertTrue("Timeout occurred.", latch.await(30, TimeUnit.SECONDS));
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}