package com.brightcove.recyclervideoview;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.PlaylistListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventEmitterImpl;
import com.brightcove.player.model.Playlist;

public class MainActivity extends AppCompatActivity {

    private Catalog catalog;
    private RecyclerView videoListView;
    private AdapterView adapterView;

    EventEmitter eventEmitter = new EventEmitterImpl();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        videoListView = (RecyclerView) findViewById(R.id.video_list_view);

        adapterView = new AdapterView();
        videoListView.setAdapter(adapterView);

        catalog = new Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
                .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
                .setPolicy(getString(R.string.sdk_demo_policy_key))
                .build();

        catalog.findPlaylistByReferenceID(getString(R.string.sdk_demo_playlist_reference), new PlaylistListener() {
            @Override
            public void onPlaylist(Playlist playlist) {
                adapterView.setVideoList(playlist.getVideos());
            }
        });

    }

    @Override
    protected void onDestroy() {
        videoListView.setAdapter(null);
        videoListView.removeAllViews();
        super.onDestroy();

    }
}
