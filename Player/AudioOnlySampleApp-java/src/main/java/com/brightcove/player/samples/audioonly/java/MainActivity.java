package com.brightcove.player.samples.audioonly.java;

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
import com.brightcove.player.model.Playlist;
import com.brightcove.player.playback.PlaybackNotification;
import com.brightcove.player.playback.PlaybackNotificationConfig;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;

/**
 * Demonstrates audio-only playback: a Brightcove playlist of audio tracks played with a
 * media-style background playback notification, plus shuffle and repeat controls.
 */
public class MainActivity extends BrightcovePlayer {

    private BrightcoveExoPlayerVideoView brightcoveVideoView;
    private RecyclerView videoListView;
    private VideoListAdapter videoListAdapter;

    private String accountId;
    private String policyKey;
    private String playListReference;
    private Catalog catalog;

    private int repeatState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountId = getString(R.string.sdk_demo_account);
        policyKey = getString(R.string.sdk_demo_policy);
        playListReference = getString(R.string.trackPlaylistReference);

        usePlaylist();
        useRepeat();
        useShuffle();

        ExoPlayerVideoDisplayComponent videoDisplayComponent = (ExoPlayerVideoDisplayComponent) brightcoveVideoView.getVideoDisplay();
        if (videoDisplayComponent != null) {
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
     * Loads a playlist of audio tracks and shows them in a list beside the player.
     */
    private void usePlaylist() {
        setContentView(R.layout.activity_main_playlist);
        brightcoveVideoView = findViewById(R.id.brightcove_video_view_playlist);
        videoListView = findViewById(R.id.video_list_view);
        videoListAdapter = new VideoListAdapter(brightcoveVideoView);
        videoListView.setAdapter(videoListAdapter);
        catalog = new Catalog.Builder(brightcoveVideoView.getEventEmitter(), accountId)
                .setPolicy(policyKey)
                .build();
        catalog.findPlaylistByReferenceID(playListReference, new PlaylistListener() {
            @Override
            public void onPlaylist(Playlist playlist) {
                brightcoveVideoView.addAll(playlist.getVideos());
                videoListAdapter.setVideoList(playlist.getVideos());
                brightcoveVideoView.start();
            }
        });
        ImageButton actionGitHubButton = findViewById(R.id.action_github);
        actionGitHubButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.GITHUB_URL)));
            startActivity(browserIntent);
        });
    }

    private void useRepeat() {
        ImageButton actionRepeatButton = findViewById(R.id.action_repeat);
        actionRepeatButton.setVisibility(View.VISIBLE);
        ExoPlayerVideoDisplayComponent exoVideoDisplayComponent = (ExoPlayerVideoDisplayComponent) brightcoveVideoView.getVideoDisplay();
        actionRepeatButton.setOnClickListener(v -> {
            if (repeatState == 0) {
                repeatState = 1;
                exoVideoDisplayComponent.getExoPlayer().setRepeatMode(Player.REPEAT_MODE_ONE);
                actionRepeatButton.setImageResource(R.drawable.ic_repeat_once_white_24dp);
            } else if (repeatState == 1) {
                repeatState = 2;
                exoVideoDisplayComponent.getExoPlayer().setRepeatMode(Player.REPEAT_MODE_ALL);
                actionRepeatButton.setImageResource(R.drawable.ic_repeat_white_24dp);
            } else if (repeatState == 2) {
                repeatState = 0;
                exoVideoDisplayComponent.getExoPlayer().setRepeatMode(Player.REPEAT_MODE_OFF);
                actionRepeatButton.setImageResource(R.drawable.ic_repeat_off_white_24dp);
            }
        });
    }

    private void useShuffle() {
        ImageButton actionShuffleButton = findViewById(R.id.action_shuffle);
        actionShuffleButton.setVisibility(View.VISIBLE);
        ExoPlayerVideoDisplayComponent exoVideoDisplayComponent = (ExoPlayerVideoDisplayComponent) brightcoveVideoView.getVideoDisplay();
        actionShuffleButton.setOnClickListener(v -> {
            actionShuffleButton.setSelected(!actionShuffleButton.isSelected());
            if (actionShuffleButton.isSelected()) {
                actionShuffleButton.setImageResource(R.drawable.ic_shuffle_green_24dp);
                exoVideoDisplayComponent.getExoPlayer().setShuffleModeEnabled(true);
            } else {
                actionShuffleButton.setImageResource(R.drawable.ic_shuffle_white_24dp);
                exoVideoDisplayComponent.getExoPlayer().setShuffleModeEnabled(false);
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
