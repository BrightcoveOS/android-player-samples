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

import com.brightcove.cast.GoogleCastComponent;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.view.BrightcoveVideoView;
import com.robotium.solo.Solo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Integration tests for the expected behavior of the Android Cast Plugin for the Brightcove
 * Native Player SDK for Android.
 * @author Billy Hnath (bhnath@brightcove.com)
 */
public class GoogleCastComponentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private final String TAG = this.getClass().getSimpleName();

    private MainActivity castActivity;
    private BrightcoveVideoView brightcoveVideoView;
    private EventEmitter eventEmitter;
    private GoogleCastComponent component;
    private GoogleCastSampleFragment googleCastSampleFragment;
    private Solo solo;

    public GoogleCastComponentTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        castActivity = getActivity();
        setActivityInitialTouchMode(false);

        solo  = new Solo(getInstrumentation(), castActivity);
        brightcoveVideoView = (BrightcoveVideoView) castActivity.findViewById(R.id.brightcove_video_view);
        eventEmitter = brightcoveVideoView.getEventEmitter();
        googleCastSampleFragment = castActivity.googleCastSampleFragment;
        castActivity.launchBrightcoveVideoViewFragment();

        brightcoveVideoView.setVideoPath("https://ia600408.us.archive.org/26/items/BigBuckBunny_328/BigBuckBunny_512kb.mp4");

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        eventEmitter.once(EventType.DID_SET_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                brightcoveVideoView.start();
                countDownLatch.countDown();
            }
        });
        assertTrue("Timeout occured.", countDownLatch.await(1, TimeUnit.MINUTES));
    }

    /**
     * Test
     * From a local playback state currently playing
     * Selecting the cast button will start playback on the remote device
     * and pause playback on the local device.
     *
     * @throws InterruptedException
     */
    public void testCastMediaToRemoteDevice() throws InterruptedException {
        Log.v(TAG, "testCastMediaToRemoteDevice");
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        //assertTrue("BrightcoveVideoView is playing.", brightcoveVideoView.isPlaying());

        solo.clickOnActionBarItem(R.id.media_router_menu_item);
        solo.clickInList(0);

        eventEmitter.on(EventType.DID_PAUSE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                assertFalse("BrightVideoVideo is paused.", brightcoveVideoView.isPlaying());
                countDownLatch.countDown();
            }
        });

        assertTrue("Timeout occurred.", countDownLatch.await(1, TimeUnit.MINUTES));
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        //solo.finishOpenedActivities();
    }
}
