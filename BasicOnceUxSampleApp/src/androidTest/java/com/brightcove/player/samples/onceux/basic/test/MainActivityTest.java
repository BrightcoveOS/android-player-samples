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
    private String adUrl = "http://onceux.unicornmedia.com/now/ads/vmap/od/auto/95ea75e1-dd2a-4aea-851a-28f46f8e8195/43f54cc0-aa6b-4b2c-b4de-63d707167bf9/9b118b95-38df-4b99-bb50-8f53d62f6ef8??umtp=0";
    private String contentUrl = "http://cdn5.unicornmedia.com/now/stitched/mp4/95ea75e1-dd2a-4aea-851a-28f46f8e8195/00000000-0000-0000-0000-000000000000/3a41c6e4-93a3-4108-8995-64ffca7b9106/9b118b95-38df-4b99-bb50-8f53d62f6ef8/0/0/105/1438852996/content.mp4";

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

    public void setWifi(final boolean state) {
        WifiManager wifiManager = (WifiManager) this.getActivity().getSystemService(Context.WIFI_SERVICE);
        boolean wifiResult = wifiManager.setWifiEnabled(state);
        Log.v(TAG, "Wifi is now enabled: " + wifiResult);
    }
    /*
    public void testNoAdDataEventDoesNotTrigger() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Log.v(TAG, "testNoAdDataURL");
        eventEmitter.once(OnceUxEventType.NO_AD_DATA_URL, new EventListener() {
                @Override
                public void processEvent(Event event) {
                    Log.v(TAG, "This should not have happened; an Ad Data URL was supplied.");
                    latch.countDown();
                }
            });
        mainActivity.getOnceUxPlugin().processVideo(adUrl, contentUrl);
        assertFalse("Test Failed.", latch.await(15, TimeUnit.SECONDS));
        brightcoveVideoView.stopPlayback();
    }
    */
    public void testAdDataReadyEvent() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        setWifi(false);
        // Turning off Wifi to trigger an error in the AD_DATA_READY event.
        Log.v(TAG, "Catching AD_DATA_READY Event.");

        eventEmitter.on(OnceUxEventType.AD_DATA_READY, new EventListener() {
                @Override
                public void processEvent(Event event) {
                    String errorMessage = (String) event.properties.get(OnceUxEventType.VMAP_ERRORS);
                    String responseMessage = (String) event.properties.get(OnceUxEventType.VMAP_RESPONSE);
                    Log.v(TAG, "AD_DATA_READY Error: " + errorMessage);
                    Log.v(TAG, "AD_DATA_READY Response: " + responseMessage);
                    if (responseMessage == null || responseMessage.equals("")) {
                        if(errorMessage == null || errorMessage.equals("")) {
                            // both are empty
                            Log.v(TAG, "Error: AD_DATA_READY is empty");
                            latch.countDown();
                        } else {
                            // response is empty, error is not
                            Log.v(TAG, "Error: AD_DATA_READY has at least one error");
                            latch.countDown();
                        }
                    } else {
                        if (responseMessage != null || !responseMessage.equals("")) {
                            // both are not empty
                            Log.v(TAG, "Error: AD_DATA_READY is too full");
                            latch.countDown();
                        } else {
                            // response not empty, error empty
                                Log.v(TAG, "This should not happen. AD_DATA_READY is ready.");
                        }
                    }
                };
            });
        mainActivity.getOnceUxPlugin().processVideo(adUrl, contentUrl);
        setWifi(true);
        assertTrue("Test Failed", latch.await(1, TimeUnit.MINUTES));
    }
}
