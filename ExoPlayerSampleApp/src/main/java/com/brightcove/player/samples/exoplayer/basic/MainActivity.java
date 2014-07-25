package com.brightcove.player.samples.exoplayer.basic;

import android.os.Bundle;
import android.util.Log;

import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.ExoPlayerVideoView;

/**
 * This app illustrates how to use the ExoPlayer with the Brightcove
 * Native Player SDK for Android.
 *
 * @author Billy Hnath (bhnath@brightcove.com)
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (ExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

//        // Add a test video to the BrightcoveVideoView.
//        Catalog catalog = new Catalog("ErQk9zUeDVLIp8Dc7aiHKq8hDMgkv5BFU7WGshTc-hpziB3BuYh28A..");
//        catalog.findPlaylistByReferenceID("stitch", new PlaylistListener() {
//            public void onPlaylist(Playlist playlist) {
//                brightcoveVideoView.addAll(playlist.getVideos());
//            }
//
//            public void onError(String error) {
//                Log.e(TAG, error);
//            }
//        });
        brightcoveVideoView.setVideoPath("https://ia600408.us.archive.org/26/items/BigBuckBunny_328/BigBuckBunny_512kb.mp4");

        // Log whether or not instance state in non-null.
        if (savedInstanceState != null) {
            Log.v(TAG, "Restoring saved position");
        } else {
            Log.v(TAG, "No saved state");
        }
    }
}