package com.brightcove.player.samples.appcompat.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.brightcove.player.appcompat.BrightcovePlayerFragment;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BaseVideoView;

public class MainFragment extends BrightcovePlayerFragment {

    public static final String TAG = MainFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_main, container, false);
        baseVideoView = (BaseVideoView) result.findViewById(R.id.brightcove_video_view);
        super.onCreateView(inflater, container, savedInstanceState);

        Video video = Video.createVideo("http://media.w3.org/2010/05/sintel/trailer.mp4", DeliveryType.MP4);
        baseVideoView.add(video);
        baseVideoView.getAnalytics().setAccount("1760897681001");
        baseVideoView.start();

        return result;
    }
}
