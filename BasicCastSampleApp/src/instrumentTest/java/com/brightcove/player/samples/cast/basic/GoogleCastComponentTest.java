/**
 * Copyright (C) 2014 Brightcove, Inc. All Rights Reserved. No
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
package com.brightcove.player.samples.cast.basic;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;

import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.view.BrightcoveVideoView;
import com.google.sample.castcompanionlibrary.cast.player.VideoCastControllerActivity;
import com.robotium.solo.Solo;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Integration tests for the expected behavior of the Chromecast using the Cast
 * plugin for the Brightcove Native Player SDK for Android.
 * @author Billy Hnath (bhnath@brightcove.com)
 */
public class GoogleCastComponentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private final String TAG = this.getClass().getSimpleName();

    private MainActivity castActivity;
    private BrightcoveVideoView brightcoveVideoView;
    private EventEmitter eventEmitter;
    private Solo solo;

    public GoogleCastComponentTest() {
        super(MainActivity.class);
    }

    /**
     * Initialize Robotium, the BrightcoveVideoView and the EventEmitter.
     * Start media playback.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        castActivity = getActivity();

        solo  = new Solo(getInstrumentation(), castActivity);
        brightcoveVideoView = (BrightcoveVideoView) castActivity.findViewById(R.id.brightcove_video_view);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        final CountDownLatch countDownLatch = new CountDownLatch(2);
        eventEmitter.once(EventType.DID_SET_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                brightcoveVideoView.start();
                countDownLatch.countDown();
            }
        });

        eventEmitter.once(EventType.DID_PLAY, new EventListener() {
            @Override
            public void processEvent(Event event) {
                assertTrue("BrightcoveVideoView is playing.", brightcoveVideoView.isPlaying());
                countDownLatch.countDown();
            }
        });

        assertTrue("Timeout occurred.", countDownLatch.await(1, TimeUnit.MINUTES));
    }

    /**
     * Test the successful local to remote playback and then remote to local
     * with the media starting in the PLAY state on the local device.
     * @throws InterruptedException
     */
    public void testCastMediaToRemoteDeviceAndBackFromPlaying() throws InterruptedException {
        Log.v(TAG, "testCastMediaToRemoteDeviceAndBackFromPlaying");
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        solo.clickOnActionBarItem(R.id.media_router_menu_item);
        solo.clickInList(0);

        eventEmitter.once(EventType.DID_PAUSE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                assertFalse("BrightVideoVideo is paused.", brightcoveVideoView.isPlaying());
                countDownLatch.countDown();
            }
        });
        solo.waitForActivity(VideoCastControllerActivity.class);
        solo.sleep(10000);

        solo.assertCurrentActivity("VideoCastControllerActivity is displayed", VideoCastControllerActivity.class);

        List<View> views = solo.getCurrentViews();
        for (View v : views) {
            if (v instanceof android.support.v7.app.MediaRouteButton) {
                solo.clickOnView(v);
            }
        }
        solo.sleep(3000);

        solo.clickOnButton("Disconnect");
        solo.sleep(3000);

        countDownLatch.countDown();

        assertTrue("Timeout occurred.", countDownLatch.await(3, TimeUnit.MINUTES));
    }

    /**
     * Test the successful local to remote playback and then remote to local
     * with the media starting in the PAUSE state on the local device.
     *
     * @throws InterruptedException
     */
    public void testCastMediaToRemoteDeviceAndBackFromPaused() throws InterruptedException {
        Log.v(TAG, "testCastMediaToRemoteDeviceAndBackFromPaused");
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        brightcoveVideoView.pause();
        solo.sleep(3000);

        eventEmitter.once(EventType.DID_STOP, new EventListener() {
            @Override
            public void processEvent(Event event) {
                assertFalse("BrightVideoVideo is not playing.", brightcoveVideoView.isPlaying());
                countDownLatch.countDown();
            }
        });

        solo.clickOnActionBarItem(R.id.media_router_menu_item);
        solo.clickInList(0);
        solo.sleep(6000);

        brightcoveVideoView.start();
        assertFalse("BrightcoveVideoView playback did not start.", brightcoveVideoView.isPlaying());

        solo.waitForActivity(VideoCastControllerActivity.class);
        solo.sleep(10000);

        solo.assertCurrentActivity("VideoCastControllerActivity is displayed", VideoCastControllerActivity.class);

        List<View> views = solo.getCurrentViews();
        for (View v : views) {
            if (v instanceof android.support.v7.app.MediaRouteButton) {
                solo.clickOnView(v);
            }
        }
        solo.sleep(3000);

        solo.clickOnButton("Disconnect");
        solo.sleep(3000);

        countDownLatch.countDown();

        assertTrue("Timeout occurred.", countDownLatch.await(3, TimeUnit.MINUTES));
    }

    /**
     * Clean up after each test.
     * @throws Exception
     */
    @Override
    public void tearDown() throws Exception {
        //solo.finishOpenedActivities();
        super.tearDown();
    }
}
