package com.brightcove.player.samples.omniture.basic;

import android.os.Bundle;
import android.util.Log;

import com.brightcove.omniture.OmnitureComponent;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.PlaylistListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;

/**
 * This app illustrates how to use the Omniture plugin with the Brightcove Player for Android.
 *
 * @author Billy Hnath
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private EventEmitter eventEmitter;
    private OmnitureComponent omnitureComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView
        // before entering the superclass. This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.omniture_activity_main );
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        eventEmitter = brightcoveVideoView.getEventEmitter();

        setupOmniture();

        // Add a test video to the BrightcoveVideoView.
        Catalog catalog = new Catalog(eventEmitter, getString(R.string.account_id), getString(R.string.policy_key));
        catalog.findPlaylistByReferenceID("play_2017_4_videos", new PlaylistListener() {
            public void onPlaylist(Playlist playlist) {
                brightcoveVideoView.addAll(playlist.getVideos());
            }

            public void onError(String error) {
                Log.e(TAG, error);
            }
        });
    }

    private void setupOmniture() {

        // Initializing the Omniture plugin with the JSON configuration file
        // and evar rulesets should be enough to get started.
        omnitureComponent = new OmnitureComponent(eventEmitter,
                this,
                "Android Sample App",
                "Android Sample Player");
    }
}
