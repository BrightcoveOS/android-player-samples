package com.brightcove.player.samples.audioonly;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.recyclerview.widget.RecyclerView;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.PlaylistListener;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.logging.Log;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.model.Video;
import com.brightcove.player.playback.MediaPlayback;
import com.brightcove.player.playback.PlaybackNotification;
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

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        accountId = getString(R.string.account);
        policyKey = getString(R.string.policy);
        videoId = getString(R.string.videoId);
        playListReference = getString(R.string.trackPlaylistReference);

        // Set this as true if you want to use a playlist
        boolean usePlaylist = false;
        // Set this as true if you want to use a customized notification
        boolean useCustomNotification = false;

        if (usePlaylist) {
            usePlaylist();
        } else {
            useSingleVideo();
        }

        if (useCustomNotification) {
            useCustomNotification();
        }
    }

    /**
     * Displays a single audio media on the UI
     */
    private void useSingleVideo() {
        setContentView(R.layout.activity_main);
        brightcoveVideoView = findViewById(R.id.brightcove_video_view);
        Catalog catalog = new Catalog.Builder(brightcoveVideoView.getEventEmitter(), accountId)
                .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
                .setPolicy(getString(R.string.policy))
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


        Catalog catalog = new Catalog.Builder(brightcoveVideoView.getEventEmitter(), accountId)
                .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
                .setPolicy(policyKey)
                .build();

        catalog.findPlaylistByReferenceID(playListReference, new PlaylistListener() {
            @Override
            public void onPlaylist(Playlist playlist) {
                brightcoveVideoView.addAll(playlist.getVideos());
                adapterView.setVideoList(playlist.getVideos());
            }
        });
    }

    /**
     * Displays a customized playback notification
     */
    private void useCustomNotification(){
        brightcoveVideoView.getPlayback().getNotification().setConfig(
                new PlaybackNotification.Config(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setUseStopAction(true)
                        .setUseNextAction(false)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setAdapter(new PlaybackNotification.MediaDescriptionAdapter() {
                            @Override
                            public CharSequence getCurrentContentTitle(MediaPlayback<?> playback) {
                                return "This is a custom title for the track";
                            }
                            @RequiresApi(api = Build.VERSION_CODES.S)
                            @Override
                            public PendingIntent createCurrentContentIntent(MediaPlayback<?> playback) {
                                Intent resultIntent = new Intent(context, SecondActivity.class);
                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                                stackBuilder.addNextIntentWithParentStack(resultIntent);
                                int pendingIntentFlag = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_MUTABLE;
                                return stackBuilder.getPendingIntent(0, pendingIntentFlag);
                            }
                            @Override
                            public Bitmap getCurrentLargeIcon(MediaPlayback playback, BitmapCallback callback) {
                                // TODO: return your bitmap
                                return null;
                            }
                        }));

    }

    @Override
    protected void onDestroy() {
        videoListView.setAdapter(null);
        videoListView.removeAllViews();
        super.onDestroy();
    }
}