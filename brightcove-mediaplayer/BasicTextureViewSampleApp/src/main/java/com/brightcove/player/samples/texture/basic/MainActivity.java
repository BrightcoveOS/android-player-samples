package com.brightcove.player.samples.texture.basic;

import android.os.Bundle;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.CatalogError;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveTextureVideoView;

import java.util.List;

/**
 * This app illustrates how to use the BrightcoveTextureVideoView.
 *
 * @author Paul Matthew Reilly (original code)
 */
public class MainActivity extends BrightcovePlayer {

    public static final String TAG = MainActivity.class.getSimpleName();
    private EventEmitter eventEmitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (BrightcoveTextureVideoView) findViewById(R.id.brightcove_video_view);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        super.onCreate(savedInstanceState);
        // Add a test video to the BrightcoveVideoView.
        Catalog catalog = new Catalog.Builder(eventEmitter, getString(R.string.account_id))
                .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL).setPolicy(getString(R.string.policy_key)).build();
        catalog.findVideoByID("4866305819001", new VideoListener() {
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
                brightcoveVideoView.start();
            };

            @Override
            public void onError(List<CatalogError> errors) {
                super.onError(errors);
            }
        });
    }
}
