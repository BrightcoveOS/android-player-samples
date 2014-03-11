package com.brightcove.player.samples.cast.basic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brightcove.cast.GoogleCastComponent;
import com.brightcove.cast.GoogleCastEventType;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.view.BrightcovePlayerFragment;
import com.brightcove.player.view.BrightcoveVideoView;
import com.google.sample.castcompanionlibrary.widgets.MiniController;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhnath on 3/10/14.
 */
public class BrightcoveVideoViewFragment extends BrightcovePlayerFragment {
    public static final String TAG = BrightcoveVideoViewFragment.class.getSimpleName();

    private EventEmitter eventEmitter;
    private GoogleCastComponent googleCastComponent;
    private BrightcoveVideoView brightcoveVideoView;
    private MiniController miniController;

    public static BrightcoveVideoViewFragment newInstance(BrightcoveVideoView videoView, EventEmitter emitter) {
        BrightcoveVideoViewFragment brightcoveVideoViewFragment = new BrightcoveVideoViewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("videoView", (Serializable) videoView);
        bundle.putSerializable("eventEmitter", (Serializable) emitter);
        brightcoveVideoViewFragment.setArguments(bundle);
        return brightcoveVideoViewFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.basic_cast_fragment, container, false);


        brightcoveVideoView = (BrightcoveVideoView) getArguments().getSerializable("videoView");
        eventEmitter = (EventEmitter) getArguments().getSerializable("eventEmitter");

        View view = super.onCreateView(inflater, container, savedInstanceState);

        String applicationId = getResources().getString(R.string.application_id);
        googleCastComponent = new GoogleCastComponent(eventEmitter, applicationId, getActivity());

        miniController = (MiniController) view.findViewById(R.id.miniController1);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(GoogleCastComponent.CAST_MINICONTROLLER, miniController);
        eventEmitter.emit(GoogleCastEventType.SET_MINI_CONTROLLER, properties);

        String url = getResources().getString(R.string.media_url);
        String bbimg = getResources().getString(R.string.media_image);
        eventEmitter.emit(GoogleCastEventType.SET_MEDIA_METADATA,
                buildMetadataProperties("subTitle", "title", "studio", bbimg, bbimg, url));

        brightcoveVideoView.setVideoPath(url);
        brightcoveVideoView.start();

        return view;
    }

    private Map<String, Object> buildMetadataProperties(String subTitle, String title, String studio, String imageUrl, String bigImageUrl, String url) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_SUBTITLE, subTitle);
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_TITLE, title);
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_STUDIO, studio);
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_IMAGE_URL, imageUrl);
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_BIG_IMAGE_URL, bigImageUrl);
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_URL, url);
        return properties;
    }

}
