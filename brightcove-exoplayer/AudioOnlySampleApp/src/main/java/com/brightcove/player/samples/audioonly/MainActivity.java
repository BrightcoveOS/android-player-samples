package com.brightcove.player.samples.audioonly;

import android.app.PendingIntent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.PlaylistListener;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventEmitterImpl;
import com.brightcove.player.logging.Log;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.model.Video;
import com.brightcove.player.playback.MediaPlayback;
import com.brightcove.player.playback.PlaybackNotification;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;

public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private Catalog catalog;
    private RecyclerView videoListView;
    private AdapterView adapterView;

    private String accountId;
    private String policyKey;
    private String trackId;
    private String trackPlayListReference;
    private Video video;

    private BrightcoveExoPlayerVideoView brightcoveVideoView;

    EventEmitter eventEmitter = new EventEmitterImpl();

    //Load the playlist Audio Only Sample
    private final Boolean isPlaylist = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountId = getString(R.string.account);
        policyKey = getString(R.string.policy);
        trackId = getString(R.string.trackId);
        trackPlayListReference = getString(R.string.trackPlaylistReference);


        //Playlist implementation
        if (isPlaylist) {
            setContentView(R.layout.activity_main_playlist);

            videoListView = (RecyclerView) findViewById(R.id.video_list_view);
            adapterView = new AdapterView();
            videoListView.setAdapter(adapterView);

            catalog = new Catalog.Builder(eventEmitter, accountId)
                    .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
                    .setPolicy(policyKey)
                    .build();

            catalog.findPlaylistByReferenceID(trackPlayListReference, new PlaylistListener() {
                @Override
                public void onPlaylist(Playlist playlist) {
                    adapterView.setVideoList(playlist.getVideos());
                    video = playlist.getVideos().get(0);
                }
            });

            brightcoveVideoView.add(video);

        }
        //Single track implementation
        else {
            setContentView(R.layout.activity_main);
            brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);

            // Get the event emitter from the SDK and create a catalog request to fetch a video from the
            // Brightcove Edge service, given a video id, an account id and a policy key.
            EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();

            Catalog catalog = new Catalog.Builder(eventEmitter, accountId)
                    .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
                    .setPolicy(getString(R.string.policy))
                    .build();

            catalog.findVideoByID(trackId, new VideoListener() {
                // Add the video found to the queue with add().
                // Start playback of the video with start().
                @Override
                public void onVideo(Video track) {
                    Log.v(TAG, "onTrack: track = " + track);
                    brightcoveVideoView.add(track);
                    brightcoveVideoView.start();
                }
            });
        }

        //Notification
/*        brightcoveVideoView.getPlayback().getNotification().setConfig(
                new PlaybackNotification.Config(this)
                        .setColor(R.color.design_default_color_secondary)
                        .setUseStopAction(true)
                        .setAdapter(new PlaybackNotification.MediaDescriptionAdapter() {
                            @Nullable
                            @Override
                            public CharSequence getCurrentContentText(MediaPlayback<?> playback) {
                                return "This is a custom description";
                            }

                            @Nullable
                            @Override
                            public CharSequence getCurrentContentTitle(MediaPlayback<?> playback) {
                                return "This is a custom title for the track";
                            }
                            @Nullable
                            @Override
                            public PendingIntent createCurrentContentIntent(MediaPlayback<?> playback) {

                                return null;
                            }
                        }));*/
    }

    @Override
    protected void onDestroy() {
        videoListView.setAdapter(null);
        videoListView.removeAllViews();
        super.onDestroy();
    }
}