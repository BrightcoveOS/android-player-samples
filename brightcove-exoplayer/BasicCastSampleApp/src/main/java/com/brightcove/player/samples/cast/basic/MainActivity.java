package com.brightcove.player.samples.cast.basic;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.View;

import com.brightcove.cast.GoogleCastComponent;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.PlaylistListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventEmitterImpl;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.model.Video;

public class MainActivity extends AppCompatActivity implements VideoListAdapter.ItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView videoListView = findViewById(R.id.video_list_view);
        final VideoListAdapter videoListAdapter = new VideoListAdapter(this);
        videoListView.setAdapter(videoListAdapter);
        EventEmitter eventEmitter = new EventEmitterImpl();
        Catalog catalog = new Catalog(eventEmitter, getString(R.string.account), getString(R.string.policy));
        catalog.findPlaylistByReferenceID(getString(R.string.playlistRefId), new PlaylistListener() {
            @Override
            public void onPlaylist(Playlist playlist) {
                videoListAdapter.setVideoList(playlist.getVideos());
            }
        });
    }

    @Override
    public void itemClicked(View view, Video video, int position) {
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(VideoPlayerActivity.INTENT_EXTRA_VIDEO_ID, video.getId());

        Pair<View, String> imagePair = Pair
                .create(view, getString(R.string.transition_image));
        //noinspection unchecked
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, imagePair);
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        GoogleCastComponent.setUpMediaRouteButton(this, menu);
        return true;
    }
}