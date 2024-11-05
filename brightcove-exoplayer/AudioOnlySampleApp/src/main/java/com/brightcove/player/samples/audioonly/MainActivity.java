package com.brightcove.player.samples.audioonly;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.media3.common.Player;
import androidx.recyclerview.widget.RecyclerView;

import com.brightcove.playback.notification.BackgroundPlaybackNotification;
import com.brightcove.player.display.ExoPlayerVideoDisplayComponent;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.PlaylistListener;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.logging.Log;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.model.Video;
import com.brightcove.player.playback.PlaybackNotification;
import com.brightcove.player.playback.PlaybackNotificationConfig;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;

public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private BrightcoveExoPlayerVideoView brightcoveVideoView;
    private RecyclerView videoListView;
    private AdapterView adapterView;

    private String accountId;
    private String policyKey;
    private String playListReference;
    private String videoId;
    private Catalog catalog;

    /* Set this as true if you want to use a playlist */
    boolean usePlaylist = true;
    /* Set this as true if you want to use the repeat options */
    boolean useRepeat = true;
    /* Set this as true if you want to use the shuffle list option */
    boolean useShuffle = true;

    private Context context;

    private int repeatState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        accountId = getString(R.string.account);
        policyKey = getString(R.string.policy);
        videoId = getString(R.string.videoId);
        playListReference = getString(R.string.trackPlaylistReference);
        if (usePlaylist) {
            usePlaylist();
        } else {
            useSingleVideo();
        }
        if (useRepeat) {
            useRepeat();
        }
        if (useShuffle) {
            useShuffle();
        }
        ExoPlayerVideoDisplayComponent videoDisplayComponent = (ExoPlayerVideoDisplayComponent) brightcoveVideoView.getVideoDisplay();
        if (videoDisplayComponent != null ) {
            if (videoDisplayComponent.getPlaybackNotification() == null) {
                videoDisplayComponent.setPlaybackNotification(createPlaybackNotification());
            }
        }
    }

    private PlaybackNotification createPlaybackNotification() {
        ExoPlayerVideoDisplayComponent displayComponent = ((ExoPlayerVideoDisplayComponent) brightcoveVideoView.getVideoDisplay());
        PlaybackNotification notification = BackgroundPlaybackNotification.getInstance(this);
        notification.setConfig(new PlaybackNotificationConfig(this));
        notification.setPlayback(displayComponent.getPlayback());
        return notification;
    }

    /**
     * Displays a single audio media on the UI
     */
    private void useSingleVideo() {
        setContentView(R.layout.activity_main);
        brightcoveVideoView = findViewById(R.id.brightcove_video_view);
        catalog = new Catalog.Builder(brightcoveVideoView.getEventEmitter(), accountId)
                .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
                .setPolicy(policyKey)
                .build();
        catalog.findVideoByID(videoId, new VideoListener() {
            @Override
            public void onVideo(Video track) {
                Log.v(TAG, "onTrack: track = " + track);
                brightcoveVideoView.add(track);
                brightcoveVideoView.start();
            }
        });
    }

    /**
     * Displays a list of audios on the UI
     */
    private void usePlaylist() {
        setContentView(R.layout.activity_main_playlist);
        brightcoveVideoView = findViewById(R.id.brightcove_video_view_playlist);
        videoListView = findViewById(R.id.video_list_view);
        adapterView = new AdapterView(brightcoveVideoView);
        videoListView.setAdapter(adapterView);
        catalog = new Catalog.Builder(brightcoveVideoView.getEventEmitter(), accountId)
                .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
                .setPolicy(policyKey)
                .build();
        catalog.findPlaylistByReferenceID(playListReference, new PlaylistListener() {
            @Override
            public void onPlaylist(Playlist playlist) {
                brightcoveVideoView.addAll(playlist.getVideos());
                adapterView.setVideoList(playlist.getVideos());
                brightcoveVideoView.start();
            }
        });
        ImageButton actionGitHubButton = findViewById(R.id.action_github);
        actionGitHubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.GITHUB_URL)));
                startActivity(browserIntent);
            }
        });
    }

    public void useRepeat(){
        if (usePlaylist) {
            ImageButton actionRepeatButton = findViewById(R.id.action_repeat);
            actionRepeatButton.setVisibility(View.VISIBLE);
            ExoPlayerVideoDisplayComponent exoVideoDisplayComponent = (ExoPlayerVideoDisplayComponent) brightcoveVideoView.getVideoDisplay();
            actionRepeatButton.setOnClickListener(v -> {
                if (repeatState == 0){
                    repeatState = 1;
                    exoVideoDisplayComponent.getExoPlayer().setRepeatMode(Player.REPEAT_MODE_ONE);
                    actionRepeatButton.setImageResource(R.drawable.ic_repeat_once_white_24dp);
                }
                else if (repeatState == 1){
                    repeatState = 2;
                    exoVideoDisplayComponent.getExoPlayer().setRepeatMode(Player.REPEAT_MODE_ALL);
                    actionRepeatButton.setImageResource(R.drawable.ic_repeat_white_24dp);
                }
                else if (repeatState == 2){
                    repeatState = 0;
                    exoVideoDisplayComponent.getExoPlayer().setRepeatMode(Player.REPEAT_MODE_OFF);
                    actionRepeatButton.setImageResource(R.drawable.ic_repeat_off_white_24dp);
                }
            });
        }
    }

    public void useShuffle(){
        if (usePlaylist) {
            ImageButton actionShuffleButton = findViewById(R.id.action_shuffle);
            actionShuffleButton.setVisibility(View.VISIBLE);
            ExoPlayerVideoDisplayComponent exoVideoDisplayComponent = (ExoPlayerVideoDisplayComponent) brightcoveVideoView.getVideoDisplay();
            actionShuffleButton.setOnClickListener(v -> {
                actionShuffleButton.setSelected(!actionShuffleButton.isSelected());
                if (actionShuffleButton.isSelected()){
                    actionShuffleButton.setImageResource(R.drawable.ic_shuffle_green_24dp);
                    exoVideoDisplayComponent.getExoPlayer().setShuffleModeEnabled(true);
                } else {
                    actionShuffleButton.setImageResource(R.drawable.ic_shuffle_white_24dp);
                    exoVideoDisplayComponent.getExoPlayer().setShuffleModeEnabled(false);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        videoListView.setAdapter(null);
        videoListView.removeAllViews();
        super.onDestroy();
    }
}