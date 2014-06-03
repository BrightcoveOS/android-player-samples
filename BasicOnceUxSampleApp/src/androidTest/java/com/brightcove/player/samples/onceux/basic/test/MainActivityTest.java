package com.brightcove.player.samples.onceux.basic.test;

import android.content.Intent;
import android.os.CountDownTimer;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.provider.Settings;
import android.widget.Toast;
import android.content.Context;

import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
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
        Log.v(TAG, "testNoAdDataURL");
        eventEmitter.on(OnceUxEventType.NO_AD_DATA_URL, new EventListener() {
            @Override
            public void processEvent(Event event) {
                fail("This should have never happened; there is no Ad Data URL.");
            }
        });
    }
}
