/**
 * Copyright (C) 2012 Brightcove, Inc. All Rights Reserved. No
 * use, copying or distribution of this work may be made except in
 * accordance with a valid license agreement from Brightcove, Inc.
 * This notice must be included on all copies, modifications and
 * derivatives of this work.
 *
 * Brightcove, Inc MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT
 * THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT. BRIGHTCOVE SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED
 * BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
 * SOFTWARE OR ITS DERIVATIVES.
 *
 * "Brightcove" is a trademark of Brightcove, Inc.
 */
package com.brightcove.player.samples.ima.basic;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import com.brightcove.ima.GoogleIMAEventType;
import com.brightcove.player.event.Default;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.view.BrightcoveVideoView;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * A white box style test that asserts that all six videos in the
 * playlist are played and that a preroll and postroll are played
 * before and after each video.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private final String TAG = this.getClass().getSimpleName();

    private enum State {
        STARTED_AD, COMPLETED_AD, STARTED_CONTENT, COMPLETED_CONTENT
    }

    private State state;
    private BrightcoveVideoView brightcoveVideoView;
    private EventEmitter eventEmitter;
    private int numPrerollsPlayed = 0;
    private int numPostrollsPlayed = 0;
    private final CountDownLatch latch = new CountDownLatch(6);

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        MainActivity mainActivity = getActivity();
        brightcoveVideoView = (BrightcoveVideoView) mainActivity.findViewById(R.id.brightcove_video_view);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        eventEmitter.on(GoogleIMAEventType.DID_START_AD, new EventListener() {
            @Override
            public void processEvent(Event event) {
                assertTrue("Should not have started an ad: " + state, state != State.STARTED_AD);
                if (state != State.STARTED_CONTENT) {
                    numPrerollsPlayed++;
                } else {
                    numPostrollsPlayed++;
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
                enableOnceProgressListener();
            }
        });

        eventEmitter.on(EventType.COMPLETED, new EventListener() {
            // Use a Default listener to only be notified after the
            // cue points have been handled.
            @Override
            @Default
            public void processEvent(Event event) {
                assertTrue("Should have played a postroll: " + state, state == State.COMPLETED_AD);
                state = State.COMPLETED_CONTENT;
                latch.countDown();
            }
        });

        eventEmitter.once(EventType.DID_SET_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                brightcoveVideoView.start();
            }
        });
    }

    public void enableOnceProgressListener() {
        eventEmitter.once(EventType.PROGRESS, new EventListener() {
            @Override
            public void processEvent(Event event) {
                assertTrue("Should have played a preroll: " + state, state == State.COMPLETED_AD);
                state = State.STARTED_CONTENT;
            }
        });
    }

    public void testPlay() throws InterruptedException {
        Log.v(TAG, "testPlay");
        assertTrue("Timeout occurred.", latch.await(7, TimeUnit.MINUTES));
        assertEquals("Should have played 6 prerolls.", 6, numPrerollsPlayed);
        assertEquals("Should have played 6 postrolls.", 6, numPostrollsPlayed);
    }
}