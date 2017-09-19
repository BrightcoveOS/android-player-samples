package com.brightcove.player.samples.onceux.basic;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.event.Event;

import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;

import com.brightcove.onceux.OnceUxComponent;
import com.brightcove.onceux.event.OnceUxEventType;

/**
 * This app illustrates how to use the Once UX plugin to ensure that:
 *
 * - player controls are hidden during ad playback,
 *
 * - tracking beacons are fired from the client side,
 *
 * - videos are clickable during ad playback and visit the appropriate website,
 *
 * - the companion banner is shown on page switched appropriately as new ads are played
 *
 * @author Paul Michael Reilly
 */
public class MainActivity extends BrightcovePlayer {

    // Private class constants

    private final String TAG = this.getClass().getSimpleName();

    // Private instance variables

    // The OnceUX plugin VMAP data URL, which tells the plugin when to
    // send tracking beacons, when to hide the player controls and
    // what the click through URL for the ads shoud be.  The VMAP data
    // will also identify what the companion ad should be and what
    // it's click through URL is.
    private String onceUxAdDataUrl = "http://once.unicornmedia.com/now/ads/vmap/od/auto/c501c3ee-7f1c-4020-aa6d-0b1ef0bbd4a9/202ef8bb-0d9d-4f6f-bd18-f45aa3010fe6/8a146f45-9fac-462e-a111-de60ec96198b/content.once";

    private OnceUxComponent plugin;
    public OnceUxComponent getOnceUxPlugin() {
        return plugin;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign brightcoveVideoView before
        // entering the superclass.  This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.onceux_activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        brightcoveVideoView.getAnalytics().setAccount("5420904993001");
        super.onCreate(savedInstanceState);

        // Setup the event handlers for the OnceUX plugin, set the companion ad container,
        // register the VMAP data URL inside the plugin and start the video.  The plugin will
        // detect that the video has been started and pause it until the ad data is ready or an
        // error condition is detected.  On either event the plugin will continue playing the
        // video.
        registerEventHandlers();
        plugin = new OnceUxComponent(this, brightcoveVideoView);
        View view = findViewById(R.id.ad_frame);
        if (view != null && view instanceof ViewGroup) {
            plugin.addCompanionContainer((ViewGroup) view);
        }
        plugin.processVideo(onceUxAdDataUrl);
   }

    // Private instance methods

    /**
     * Procedural abstraction used to setup event handlers for the OnceUX plugin.
     */
    private void registerEventHandlers() {
        // Handle the case where the ad data URL has not been supplied to the plugin.
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();
        eventEmitter.on(OnceUxEventType.NO_AD_DATA_URL, new EventListener() {
            @Override
            public void processEvent(Event event) {
                // Log the event and display a warning message (later)
                Log.e(TAG, event.getType());
                // TODO: throw up a stock Android warning widget.
            }
        });
    }
}
