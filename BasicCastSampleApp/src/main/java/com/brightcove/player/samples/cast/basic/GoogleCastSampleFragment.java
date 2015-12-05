package com.brightcove.player.samples.cast.basic;

import android.os.Bundle;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brightcove.cast.GoogleCastComponent;
import com.brightcove.cast.GoogleCastEventType;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.view.BrightcovePlayerFragment;
import com.brightcove.player.view.BrightcoveVideoView;
import com.google.android.libraries.cast.companionlibrary.widgets.MiniController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhnath on 3/10/14.
 */
public class GoogleCastSampleFragment extends BrightcovePlayerFragment {
    public static final String TAG = GoogleCastSampleFragment.class.getSimpleName();

    private GoogleCastComponent googleCastComponent;
    private MiniController miniController;
    private EventEmitter eventEmitter;

    public GoogleCastSampleFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize the Cast Companion Library's VideCastManager, so
        // the MiniController can find it when it's inflated.
        String applicationId = getResources().getString(R.string.application_id);
        GoogleCastComponent.initializeVideoCastManager(getActivity(), applicationId, null);

        // Perform the internal wiring to be able to make use of the BrightcovePlayerFragment.
        View view = inflater.inflate(R.layout.basic_cast_fragment, container, false);
        brightcoveVideoView = (BrightcoveVideoView) view.findViewById(R.id.brightcove_video_view);
        eventEmitter = brightcoveVideoView.getEventEmitter();
        super.onCreateView(inflater, container, savedInstanceState);

        // Initialize the android_cast_plugin which requires the application id of your Cast
        // receiver application.
        googleCastComponent = new GoogleCastComponent(eventEmitter, applicationId, getActivity());

        // Initialize the MiniController widget which will allow control of remote media playback.
        miniController = (MiniController) view.findViewById(R.id.miniController1);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(GoogleCastComponent.CAST_MINICONTROLLER, miniController);
        eventEmitter.emit(GoogleCastEventType.SET_MINI_CONTROLLER, properties);

        // Send the location of the media (url) and its metadata information for remote playback.
        Resources resources = getResources();
        String title = resources.getString(R.string.media_title);
        String studio = resources.getString(R.string.media_studio);
        String url = resources.getString(R.string.media_url);
        String thumbnailUrl = resources.getString(R.string.media_thumbnail);
        String imageUrl = resources.getString(R.string.media_image);
        eventEmitter.emit(GoogleCastEventType.SET_MEDIA_METADATA,
                buildMetadataProperties("subTitle", title, studio, thumbnailUrl, imageUrl, url));

        brightcoveVideoView.setVideoPath(url);

        return view;
    }

    private Map<String, Object> buildMetadataProperties(String subTitle, String title, String studio,
                                                        String imageUrl, String bigImageUrl, String url) {
        Log.v(TAG, "buildMetadataProperties: subTitle " + subTitle + ", title: " + title
                + ", studio: " + studio + ", imageUrl: " + imageUrl + ", bigImageUrl: " + bigImageUrl
                + ", url: " + url);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_SUBTITLE, subTitle);
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_TITLE, title);
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_STUDIO, studio);
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_IMAGE_URL, imageUrl);
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_BIG_IMAGE_URL, bigImageUrl);
        properties.put(GoogleCastComponent.CAST_MEDIA_METADATA_URL, url);
        return properties;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(GoogleCastComponent.CAST_MENU, menu);
        properties.put(GoogleCastComponent.CAST_MENU_RESOURCE_ID, R.id.media_router_menu_item);
        eventEmitter.emit(GoogleCastEventType.SET_CAST_BUTTON, properties);
    }
}
