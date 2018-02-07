package com.brightcove.player.samples.pictureinpicture;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.model.Video;
import com.brightcove.player.pictureinpicture.PictureInPictureManager;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;

public class MainActivity extends BrightcovePlayer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        // Get the event emitter from the SDK and create a catalog request to fetch a video from the
        // Brightcove Edge service, given a video id, an account id and a policy key.
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();
        Catalog catalog = new Catalog(eventEmitter, getString(R.string.account), getString(R.string.policy));

        catalog.findVideoByID(getString(R.string.videoId), new VideoListener() {

            // Add the video found to the queue with add().
            // Start playback of the video with start().
            @Override
            public void onVideo(Video video) {
                Log.v(TAG, "onVideo: video = " + video);
                brightcoveVideoView.add(video);
                brightcoveVideoView.start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SettingsModel settingsModel = new SettingsModel(this);
        //Configure Picture in Picture
        PictureInPictureManager manager = PictureInPictureManager.getInstance();
        manager.setClosedCaptionsEnabled(settingsModel.isPictureInPictureClosedCaptionsEnabled())
                .setOnUserLeaveEnabled(settingsModel.isPictureInPictureOnUserLeaveEnabled())
                .setClosedCaptionsReductionScaleFactor(settingsModel.getPictureInPictureCCScaleFactor())
                .setAspectRatio(settingsModel.getPictureInPictureAspectRatio());
    }

    public void onClickConfigurePictureInPicture(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this,
                    "Picture-in-Picture is currently available only on Android Oreo",
                    Toast.LENGTH_LONG).show();
        }
    }
}

