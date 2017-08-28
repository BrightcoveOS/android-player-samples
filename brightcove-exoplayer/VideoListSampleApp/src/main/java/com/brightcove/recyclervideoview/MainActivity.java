package com.brightcove.recyclervideoview;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.PlaylistListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventEmitterImpl;
import com.brightcove.player.model.Playlist;

public class MainActivity extends AppCompatActivity {

    private static final String ACCOUNT_ID = "3636334163001";
    private static final String POLICY_KEY = "BCpkADawqM1ZEczFURHXFgdyqvGPj4GWHEQka6QIs7hOwSFPffq-UI_ntgaa29FEMKY87rAd0jptOZHueqyDxjVBTGmfek97TLHSLPqJKWAmKx_YSiHdLdLp8uq9hhwTWs6qJIOkN9cA0qqp";
    private static final String PLAYLIST_REF = "Play2017";


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

        catalog = new Catalog(eventEmitter, ACCOUNT_ID, POLICY_KEY);
        catalog.findPlaylistByReferenceID(PLAYLIST_REF, new PlaylistListener() {
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
