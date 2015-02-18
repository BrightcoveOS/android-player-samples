package com.brightcove.player.samples.texture.basic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import com.brightcove.player.media.DeliveryType;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveTextureVideoView;

/**
 * This app illustrates how to use the BrightcoveTextureVideoView.
 *
 * @author Paul Matthew Reilly (original code)
 */
public class MainActivity extends BrightcovePlayer {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (BrightcoveTextureVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        Video video = Video.createVideo("http://media.w3.org/2010/05/sintel/trailer.mp4", DeliveryType.MP4);
        brightcoveVideoView.add(video);
        brightcoveVideoView.start();
    }
}
