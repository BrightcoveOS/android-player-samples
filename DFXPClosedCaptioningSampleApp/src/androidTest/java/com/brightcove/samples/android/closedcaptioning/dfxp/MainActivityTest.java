package com.brightcove.samples.android.closedcaptioning.dfxp;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.view.BrightcoveVideoView;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * White box style tests for DFXP/TTML closed captioning in the Brightcove SDK.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private final String TAG = this.getClass().getSimpleName();

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
    public void testCaptionsRendered() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(2);
        Log.v(TAG, "testCaptionsRendered");

        assertTrue("Timeout occurred.", latch.await(2, TimeUnit.MINUTES));
    }
}