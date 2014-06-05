package com.brightcove.player.samples.onceux.basic.test;

import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.content.Context;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.event.Event;
import com.brightcove.player.samples.onceux.basic.MainActivity;
import com.brightcove.player.samples.onceux.basic.R;
import com.brightcove.player.view.BrightcoveVideoView;
import com.brightcove.plugin.onceux.event.OnceUxEventType;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private final String TAG = this.getClass().getSimpleName();

    private enum State {
        STARTED_AD, COMPLETED_AD, STARTING_CONTENT, STARTED_CONTENT, COMPLETED_CONTENT
    }

    private State state;
    private BrightcoveVideoView brightcoveVideoView;
    private EventEmitter eventEmitter;
    private MainActivity mainActivity;

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
    }

    public void testNoAdData() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Log.v(TAG, "testNoAdDataURL");
        mainActivity.getOnceUxPlugin().processVideo(null, "http://cdn5.unicornmedia.com/now/stitched/mp4/95ea75e1-dd2a-4aea-851a-28f46f8e8195/00000000-0000-0000-0000-000000000000/3a41c6e4-93a3-4108-8995-64ffca7b9106/9b118b95-38df-4b99-bb50-8f53d62f6ef8/0/0/105/1438852996/content.mp4");

        eventEmitter.once(OnceUxEventType.NO_AD_DATA_URL, new EventListener() {
                @Override
                public void processEvent(Event event) {
                    Log.v(TAG, "NO_AD_DATA_URL event triggered");
                    latch.countDown();
                }
            });

        eventEmitter.emit(EventType.PLAY);

        latch.await(); {
            fail("No Ad Data URL.");
        }

        brightcoveVideoView.stopPlayback();
    }

    public void testWifiOff() throws InterruptedException {
        WifiManager wifiManager = (WifiManager) this.getActivity().getSystemService(Context.WIFI_SERVICE);
        boolean wifiResult = wifiManager.setWifiEnabled(false);
        Log.v(TAG, "Wifi should be off for next test: " + wifiResult);
        //Turning off Wifi to trigger an Error in the next test for the AD_DATA_READY.
    }

    public void testTimer() throws InterruptedException {
        Log.v(TAG, "Timer!");
        final CountDownTimer timer = new CountDownTimer(1000,1000) {
                @Override
                public void onTick(long millisUntilFinished){}
                @Override
                public void onFinish() {
                    fail("Needs Ad Data");
                }
            };
        
        // We want to have another listener that is listening for the AD_READY, and inside that listener, we're going to do something!!!
        eventEmitter.on(OnceUxEventType.AD_DATA_READY, new EventListener() {
                @Override
                public void processEvent(Event event) {
                    timer.cancel();
                    String errorMessage = (String) event.properties.get(OnceUxEventType.VMAP_ERRORS);
                    String responseMessage = (String) event.properties.get(OnceUxEventType.VMAP_RESPONSE);
                    if (errorMessage == null && responseMessage == null) {
                        fail("Error: AD_DATA_READY is empty");
                    } else if(errorMessage != null && responseMessage != null) {
                        fail("Error: AD_DATA_READY is too full");
                    } else if(errorMessage != null) {
                        fail("Error: AD_DATA_READY has at least one error");
                    } else {
                        assertTrue(true);
                    }
                };
            });
    }
        public void testWifiOn() throws InterruptedException {
            WifiManager wifiManager = (WifiManager) this.getActivity().getSystemService(Context.WIFI_SERVICE);
            boolean wifiResult = wifiManager.setWifiEnabled(true);
            Log.v(TAG, "Turn Wifi back on: " + wifiResult);
        }
}
