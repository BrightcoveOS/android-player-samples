package com.brightcove.player.samples.appcompat.java;

import android.os.Bundle;
import com.brightcove.player.appcompat.BrightcovePlayerActivity;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Video;

/**
 * Demonstrates the AppCompat plugin by extending BrightcovePlayerActivity.
 */
public class AppCompatPlayerActivity extends BrightcovePlayerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Video video = Video.createVideo("https://media.w3.org/2010/05/sintel/trailer.mp4", DeliveryType.MP4);
        baseVideoView.add(video);
        baseVideoView.getAnalytics().setAccount("1760897681001");
        baseVideoView.start();
    }
}
