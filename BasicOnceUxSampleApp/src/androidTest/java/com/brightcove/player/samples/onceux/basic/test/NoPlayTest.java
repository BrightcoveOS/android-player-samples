package com.brightcove.player.samples.onceux.basic.test;

import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.content.Context;
import android.media.MediaPlayer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.event.Event;
import com.brightcove.player.samples.onceux.basic.MainActivity;
import com.brightcove.player.samples.onceux.basic.R;
import com.brightcove.player.view.BrightcoveVideoView;
import com.brightcove.plugin.onceux.event.OnceUxEventType;
import com.brightcove.player.display.VideoDisplayComponent;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private final String TAG = this.getClass().getSimpleName();

    private BrightcoveVideoView brightcoveVideoView;

    private EventEmitter eventEmitter;
    private MainActivity mainActivity;
    private String adUrl = "http://onceux.unicornmedia.com/now/ads/vmap/od/auto/95ea75e1-dd2a-4aea-851a-28f46f8e8195/43f54cc0-aa6b-4b2c-b4de-63d707167bf9/9b118b95-38df-4b99-bb50-8f53d62f6ef8??umtp=0";
    private String contentUrl = "http://cdn5.unicornmedia.com/now/stitched/mp4/95ea75e1-dd2a-4aea-851a-28f46f8e8195/00000000-0000-0000-0000-000000000000/3a41c6e4-93a3-4108-8995-64ffca7b9106/9b118b95-38df-4b99-bb50-8f53d62f6ef8/0/0/105/1438852996/content.mp4";
    private VideoDisplayComponent videoDisplay;
    private int playheadPosition;
    private int progress;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mainActivity = getActivity();
        brightcoveVideoView = (BrightcoveVideoView) mainActivity.findViewById(R.id.brightcove_video_view);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        eventEmitter.once(EventType.DID_SET_VIDEO, new EventListener() {
                @Override
                public void processEvent(Event event) {
                    brightcoveVideoView.start();
                }
            });
        eventEmitter.on(EventType.PROGRESS, new EventListener() {
                @Override
                public void processEvent(Event event) {
                    progress = event.getIntegerProperty(Event.PLAYHEAD_POSITION);
                    Log.v(TAG, "position at: " + progress);
                }
            });

    }

    public void testPlayDidNotPlay() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Log.v(TAG, "Checking for DID_PLAY event.");
        eventEmitter.once(EventType.DID_PLAY, new EventListener() {
                @Override
                public void processEvent(Event event) {
                    latch.countDown();
                    Log.v(TAG, "DID_PLAY event triggered, but it should not be playing.");
                }
            });
        mainActivity.getOnceUxPlugin().processVideo(adUrl, contentUrl);
        assertFalse("Test Failed, DID_PLAY triggered unprompted.", latch.await(30, TimeUnit.SECONDS));
        brightcoveVideoView.stopPlayback();
    }
