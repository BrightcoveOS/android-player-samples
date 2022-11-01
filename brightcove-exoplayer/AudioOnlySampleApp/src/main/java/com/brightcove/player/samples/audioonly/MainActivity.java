package com.brightcove.player.samples.audioonly;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.brightcove.player.display.ExoPlayerVideoDisplayComponent;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.PlaylistListener;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.logging.Log;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.model.Video;
import com.brightcove.player.playback.ExoMediaPlayback;
import com.brightcove.player.playback.MediaPlayback;
import com.brightcove.player.playback.PlaybackNotification;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;
import com.google.android.exoplayer2.Player;

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

    // Set this as true if you want to use a playlist
    boolean usePlaylist = true;
    // Set this as true if you want to use a customized notification
    boolean useCustomNotification = false;
    // Set this as true if you want to use the repeat options
    boolean useRepeat = true;
    // Set this as true if you want to use the shuffle list option
    boolean useShuffle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountId = getString(R.string.account);
        policyKey = getString(R.string.policy);
        videoId = getString(R.string.videoId);
        playListReference = getString(R.string.trackPlaylistReference);

        if (usePlaylist) {
            usePlaylist();
        } else {
            useSingleVideo();
        }

        if (useCustomNotification) {
            useCustomizedNotification();
        }
        if (useRepeat) {
            useRepeat();
        }
        if (useShuffle) {
            useShuffle();
        }
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
    }

    /**
     * Displays a customized playback notification
     */
    private void useCustomizedNotification(){
        brightcoveVideoView.getPlayback().getNotification().setConfig(
                new PlaybackNotification.Config(this)
                        //Change the small icon in the notification.
                        .setSmallIcon(R.mipmap.ic_launcher)
                        //Active or dismiss the Stop Action
                        .setUseStopAction(true)
                        //Active or dismiss the Next Action
                        .setUseNextAction(false)
                        //Change the color
                        .setColor(R.color.yellow)
                        //Set the Priority
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        //Modify the adapter of the notification.
                        .setAdapter(new PlaybackNotification.MediaDescriptionAdapter() {
                            //Set a custom content title
                            @Override
                            public CharSequence getCurrentContentTitle(MediaPlayback<?> playback) {
                                return "This is a custom title for the track";
                            }
                            //Set a custom icon for the adapter
                            @Override
                            public Bitmap getCurrentLargeIcon(MediaPlayback playback, BitmapCallback callback) {
                                return BitmapFactory.decodeResource(null, R.mipmap.ic_launcher);
                            }
                        }));
    }

    public void useRepeat(){
        if (usePlaylist) {
            LinearLayout linearLayoutRepeat = findViewById(R.id.linearLayoutRepeat);
            linearLayoutRepeat.setVisibility(View.VISIBLE);
            RadioGroup radioGroup = findViewById(R.id.radioGroupRepeat);
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                RadioButton radioButton = group.findViewById(checkedId);
                ExoPlayerVideoDisplayComponent exoVideoDisplayComponent = (ExoPlayerVideoDisplayComponent) brightcoveVideoView.getVideoDisplay();
                switch (radioButton.getId()) {
                    case R.id.radioButtonRepeatOff:
                        //Repeat mode deactivated
                        exoVideoDisplayComponent.getExoPlayer().setRepeatMode(Player.REPEAT_MODE_OFF);
                        break;
                    case R.id.radioButtonRepeatOne:
                        //Repeat one mode activatedREPEAT_MODE_ONE
                        exoVideoDisplayComponent.getExoPlayer().setRepeatMode(Player.REPEAT_MODE_ONE);
                        break;
                    case R.id.radioButtonRepeatAll:
                        //Repeat all mode activated
                        exoVideoDisplayComponent.getExoPlayer().setRepeatMode(Player.REPEAT_MODE_ALL);
                        break;
                    default:
                        break;
                }
            });
        }
    }

    public void useShuffle(){
        if (usePlaylist) {
            ToggleButton shuffleButton = findViewById(R.id.toggleButtonShuffle);
            shuffleButton.setVisibility(View.VISIBLE);
            ExoPlayerVideoDisplayComponent exoVideoDisplayComponent = (ExoPlayerVideoDisplayComponent) brightcoveVideoView.getVideoDisplay();
            shuffleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                exoVideoDisplayComponent.getExoPlayer().setShuffleModeEnabled(isChecked);
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