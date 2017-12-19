package com.brightcove.player.samples.appcompat.activity;

import android.os.Bundle;
import com.brightcove.player.appcompat.BrightcovePlayerActivity;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BaseVideoView;

/**
 * Example of how to use the appcompat plugin.
 */
public class MainActivity extends BrightcovePlayerActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Video video = Video.createVideo("http://media.w3.org/2010/05/sintel/trailer.mp4", DeliveryType.MP4);
        baseVideoView.add(video);
        baseVideoView.getAnalytics().setAccount("1760897681001");
        baseVideoView.start();
    }
}
