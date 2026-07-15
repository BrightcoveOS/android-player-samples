package com.brightcove.player.samples.appcompat.java;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.brightcove.player.appcompat.BrightcovePlayerFragment;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Video;

/**
 * BrightcovePlayerFragment that loads and plays the sample video.
 */
public class PlayerFragment extends BrightcovePlayerFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_player, container, false);
        baseVideoView = result.findViewById(R.id.brightcove_video_view);
        super.onCreateView(inflater, container, savedInstanceState);

        Video video = Video.createVideo(getString(R.string.sdk_demo_video_url), DeliveryType.MP4);
        baseVideoView.add(video);
        baseVideoView.getAnalytics().setAccount(getString(R.string.sdk_demo_account));
        baseVideoView.start();

        return result;
    }
}
