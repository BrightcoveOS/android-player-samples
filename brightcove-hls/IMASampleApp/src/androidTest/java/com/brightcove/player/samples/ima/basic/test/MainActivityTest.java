package com.brightcove.player.samples.ima.hls.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.brightcove.ima.GoogleIMAEventType;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.view.SeamlessVideoView;
import com.brightcove.player.samples.ima.hls.MainActivity;
import com.brightcove.player.samples.ima.hls.R;

/**
 * White box style tests for the Google IMA plugin sample app.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private final String TAG = this.getClass().getSimpleName();

    private enum State {
        STARTED_AD, COMPLETED_AD, STARTING_CONTENT, STARTED_CONTENT, COMPLETED_CONTENT
    }

    private State state;
    private SeamlessVideoView brightcoveVideoView;
    private EventEmitter eventEmitter;
    private int numPrerollsPlayed = 0;
    private int numMidrollsPlayed = 0;
    private int numPostrollsPlayed = 0;
    private MainActivity mainActivity;
    private boolean hasResumed = false;
    private int playheadPosition = 0;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        mainActivity = getActivity();
        brightcoveVideoView = (SeamlessVideoView) mainActivity.findViewById(R.id.brightcove_video_view);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        eventEmitter.once(EventType.DID_SET_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                brightcoveVideoView.start();
            }
        });
    }

    /**
     * Test for pre, mid, and post rolls.
     */
    public void testPlay() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(2);
        Log.v(TAG, "testPlay");

        eventEmitter.on(GoogleIMAEventType.DID_START_AD, new EventListener() {
            @Override
            public void processEvent(Event event) {
                assertTrue("Should not have started an ad: " + state, state != State.STARTED_AD);

                switch (state) {
                case STARTING_CONTENT:
                    numPrerollsPlayed++;
                    break;
                case STARTED_CONTENT:
                    numMidrollsPlayed++;
                    break;
                case COMPLETED_CONTENT:
                    numPostrollsPlayed++;
                    break;
                default:
                    Log.e(TAG, "Unexpected state: " + state);
                }

                state = State.STARTED_AD;
            }
        });

        eventEmitter.on(GoogleIMAEventType.DID_COMPLETE_AD, new EventListener() {
            @Override
            public void processEvent(Event event) {
                assertTrue("Should have started an ad: " + state, state == State.STARTED_AD);
                state = State.COMPLETED_AD;
            }
        });

        eventEmitter.on(EventType.WILL_CHANGE_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                state = State.STARTING_CONTENT;

                if (eventEmitter != null) {
                    eventEmitter.once(EventType.PROGRESS, new EventListener() {
                        @Override
                        public void processEvent(Event event) {
                            assertTrue("Should have played a preroll: " + state, state == State.COMPLETED_AD);
                            state = State.STARTED_CONTENT;
                        }
                    });
                }
            }
        });

        eventEmitter.on(EventType.COMPLETED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (event.properties.containsKey(Event.SKIP_CUE_POINTS)) {
                    latch.countDown();
                } else {
                    state = State.COMPLETED_CONTENT;
                }
            }
        });

        assertTrue("Timeout occurred.", latch.await(3, TimeUnit.MINUTES));
        brightcoveVideoView.stopPlayback();
        assertEquals("Should have played 2 prerolls.", 2, numPrerollsPlayed);
        assertEquals("Should have played 2 midrolls.", 2, numMidrollsPlayed);
        assertEquals("Should have played 2 postrolls.", 2, numPostrollsPlayed);
    }

    /**
     * Test pausing and resuming during content playback.
     */
    public void testPlaybackPauseResume() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        eventEmitter.on(EventType.PROGRESS, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (!hasResumed) {
                    if (event.getIntegerProperty(Event.PLAYHEAD_POSITION) > 5000) {
                        // Using a new thread, so we aren't sleeping the main thread.
                        new Thread() {
                            public void run() {
                                Instrumentation instrumentation = MainActivityTest.this.getInstrumentation();
                                instrumentation.callActivityOnPause(mainActivity);
                                Log.v(TAG, "paused");
                                MainActivityTest.this.sleep();
                                instrumentation.callActivityOnResume(mainActivity);
                                hasResumed = true;
                                Log.v(TAG, "resumed");
                            }
                        }.start();
                    }
                } else {
                    playheadPosition = event.getIntegerProperty(Event.PLAYHEAD_POSITION);
                    latch.countDown();
                }
            }
        });

        assertTrue("Timeout occurred.", latch.await(1, TimeUnit.MINUTES));
        assertTrue("Should have resumed.", hasResumed);
        assertTrue("Should not have repeated first five seconds: " + playheadPosition, playheadPosition > 5000);
    }

    /**
     * Test pausing and resuming during ad playback.
     */
    public void testAdPauseResume() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        eventEmitter.on(GoogleIMAEventType.DID_START_AD, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Instrumentation instrumentation = MainActivityTest.this.getInstrumentation();
                instrumentation.callActivityOnPause(mainActivity);
                Log.v(TAG, "paused");
                instrumentation.callActivityOnResume(mainActivity);
                hasResumed = true;
                Log.v(TAG, "resumed");
            }
        });

        eventEmitter.on(GoogleIMAEventType.DID_COMPLETE_AD, new EventListener() {
            @Override
            public void processEvent(Event event) {
                latch.countDown();
            }
        });

        assertTrue("Timeout occurred.", latch.await(1, TimeUnit.MINUTES));
        assertTrue("Should have resumed.", hasResumed);
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
