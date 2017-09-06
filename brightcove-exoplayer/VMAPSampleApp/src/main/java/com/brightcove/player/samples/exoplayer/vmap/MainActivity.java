package com.brightcove.player.samples.exoplayer.vmap;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;
import com.brightcove.player.media.DeliveryType;
import com.brightcove.player.media.VideoFields;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.vmap.VMAPComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example of how to use the VMAP plugin.
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private VMAPComponent vmapComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        vmapComponent = new VMAPComponent(brightcoveVideoView);

        View view = findViewById(R.id.ad_frame);

        if ((view != null) && (view instanceof ViewGroup)) {
            vmapComponent.addCompanionContainer((ViewGroup) view);
        } else {
            Log.e(TAG, "Companion container must be an instance of a ViewGroup");
        }

        Video video = Video.createVideo("http://media.w3.org/2010/05/sintel/trailer.mp4", DeliveryType.MP4);
        video.getProperties().put(Video.Fields.PUBLISHER_ID, "5420904993001");
        video.getProperties().put(VMAPComponent.VMAP_URL, "http://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpost&cmsid=496&vid=short_onecue&correlator=");

        brightcoveVideoView.add(video);
        brightcoveVideoView.start();
    }
}
