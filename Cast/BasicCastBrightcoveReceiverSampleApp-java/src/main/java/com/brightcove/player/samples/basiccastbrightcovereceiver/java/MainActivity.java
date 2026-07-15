package com.brightcove.player.samples.basiccastbrightcovereceiver.java;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.brightcove.cast.DefaultSessionManagerListener;
import com.brightcove.cast.GoogleCastComponent;
import com.brightcove.cast.model.SplashScreen;
import com.brightcove.cast.util.BrightcoveChannelUtil;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.PlaylistListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventEmitterImpl;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.model.Video;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.Session;

import static com.brightcove.player.samples.basiccastbrightcovereceiver.java.Constants.INTENT_EXTRA_AD_CONFIG_ID;
import static com.brightcove.player.samples.basiccastbrightcovereceiver.java.Constants.INTENT_EXTRA_VIDEO_ID;

/**
 * Launcher screen for the Cast sample: loads a Video Cloud playlist, lists it in a
 * RecyclerView, and hands the selected video to {@link VideoPlayerActivity} for local
 * or Cast playback, optionally with server-side ad insertion.
 */
public class MainActivity extends AppCompatActivity implements VideoListAdapter.ItemClickListener {

    CheckBox chkBoxPlayVideoWithSsai;
    String adConfigId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chkBoxPlayVideoWithSsai = findViewById(R.id.chkBoxPlayVideoWithSsai);

        final RecyclerView videoListView = findViewById(R.id.video_list_view);
        final VideoListAdapter videoListAdapter = new VideoListAdapter(this);
        videoListView.setAdapter(videoListAdapter);
        EventEmitter eventEmitter = new EventEmitterImpl();

        Catalog catalog = new Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
                .setPolicy(getString(R.string.sdk_demo_policy))
                .build();

        catalog.findPlaylistByReferenceID(getString(R.string.playlistReferenceId), new PlaylistListener() {
            @Override
            public void onPlaylist(Playlist playlist) {
                videoListAdapter.setVideoList(playlist.getVideos());
            }
        });

        CastContext castContext = CastContext.getSharedInstance();
        if (castContext != null) {
            castContext.getSessionManager().addSessionManagerListener(defaultSessionManagerListener);
        }

        chkBoxPlayVideoWithSsai.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                adConfigId = getString(R.string.sdk_demo_ad_config_id);
            } else {
                adConfigId = "";
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CastContext castContext = CastContext.getSharedInstance();
        if (castContext != null) {
            castContext.getSessionManager().removeSessionManagerListener(defaultSessionManagerListener);
        }
    }

    @Override
    public void itemClicked(View view, Video video, int position) {
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(INTENT_EXTRA_VIDEO_ID, video.getId());
        intent.putExtra(INTENT_EXTRA_AD_CONFIG_ID, adConfigId);
        Pair<View, String> imagePair = Pair.create(view, getString(R.string.transition_image));
        //noinspection unchecked
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imagePair);
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        GoogleCastComponent.setUpMediaRouteButton(this, menu);
        return true;
    }

    private DefaultSessionManagerListener defaultSessionManagerListener = new DefaultSessionManagerListener() {
        @Override
        public void onSessionStarted(Session castSession, String s) {
            super.onSessionStarted(castSession, s);
            String src = getString(R.string.sdk_demo_splash_screen_url);
            BrightcoveChannelUtil.castSplashScreen((CastSession) castSession, new SplashScreen(src));
        }
    };
}