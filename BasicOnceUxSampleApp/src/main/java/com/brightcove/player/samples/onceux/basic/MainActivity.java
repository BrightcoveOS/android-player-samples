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
import com.brightcove.player.view.BrightcoveVideoView;

import com.brightcove.plugin.onceux.OnceUxPlugin;
import com.brightcove.plugin.onceux.event.OnceUxEventType;

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
 * It also covers ensuring that an ad server URL accompanies the content URL.
 *
 * @author Paul Michael Reilly
 */
public class MainActivity extends BrightcovePlayer {

    // Private class constants

    private final String TAG = this.getClass().getSimpleName();

    // Private instance variables

    //Provide a pair of URLs, one for the VMAP data that will tell the plugin when to send
    //tracking beacons, when to hide the player controls and what the click through URL for the
    //ads shoud be.  The VMAP data will also identify what the componion ad should be and what
    //it's click through URL is.

    // The OnceUX plugin VMAP data URL.
    private String onceUxAdDataUrl = "http://onceux.unicornmedia.com/now/ads/vmap/od/auto/95ea75e1-dd2a-4aea-851a-28f46f8e8195/43f54cc0-aa6b-4b2c-b4de-63d707167bf9/9b118b95-38df-4b99-bb50-8f53d62f6ef8??umtp=0";
    // Original from Criss: "http://onceux.unicornmedia.com/now/ads/vmap/od/auto/b11dbc9b-9d90-4edb-b4ab-769e0049209b/2455340c-8dcd-412e-a917-c6fadfe268c7/3a41c6e4-93a3-4108-8995-64ffca7b9106/bigbuckbunny?umtp=0";

    // The OnceUX plugin content URL.
    private String onceUxContentUrl = "http://cdn5.unicornmedia.com/now/stitched/mp4/95ea75e1-dd2a-4aea-851a-28f46f8e8195/00000000-0000-0000-0000-000000000000/3a41c6e4-93a3-4108-8995-64ffca7b9106/9b118b95-38df-4b99-bb50-8f53d62f6ef8/0/0/105/1438852996/content.mp4";
    // Content suggested by Unicorn for the data url: "http://once.unicornmedia.com/now/od/auto/95ea75e1-dd2a-4aea-851a-28f46f8e8195/43f54cc0-aa6b-4b2c-b4de-63d707167bf9/9b118b95-38df-4b99-bb50-8f53d62f6ef8/content.once";
    // Original from Criss: "http://api16-phx.unicornmedia.com/now/stitched/mp4/b11dbc9b-9d90-4edb-b4ab-769e0049209b/2455340c-8dcd-412e-a917-c6fadfe268c7/3a41c6e4-93a3-4108-8995-64ffca7b9106/18bed8d5-15ec-40c7-8ac8-dd38db9832d9/content.mp4?oasid=e277545e-9b0f-4af8-bf88-6034af781892&umtp=0";

    private OnceUxPlugin plugin;
    public OnceUxPlugin getOnceUxPlugin(){
        return plugin;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.onceux_activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        // Setup the event handlers for the OnceUX plugin, set the companion ad container,
        // register the VMAP data URL inside the plugin and start the video.  The plugin will
        // detect that the video has been started and pause it until the ad data is ready or an
        // error condition is detected.  On either event the plugin will continue playing the
        // video.
        registerEventHandlers();
        plugin = new OnceUxPlugin(this, brightcoveVideoView);
        View view = findViewById(R.id.ad_frame);
        if (view != null && view instanceof ViewGroup) {
            plugin.addCompanionContainer((ViewGroup) view);
        }
        plugin.processVideo(onceUxAdDataUrl, onceUxContentUrl);

 
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
