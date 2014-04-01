package com.brightcove.player.samples.cast.basic;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brightcove.cast.GoogleCastComponent;
import com.brightcove.cast.GoogleCastEventType;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.view.BrightcovePlayerFragment;
import com.brightcove.player.view.BrightcoveVideoView;
import com.google.sample.castcompanionlibrary.widgets.MiniController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhnath on 3/10/14.
 */
public class GoogleCastSampleFragment extends BrightcovePlayerFragment {
    public static final String TAG = GoogleCastSampleFragment.class.getSimpleName();

    private static EventEmitter eventEmitter;
    private static Context context;
    private GoogleCastComponent googleCastComponent;
    private MiniController miniController;

    /**
     * Static initializer method for the fragment to get easy access to the EventEmitter
     * and Context from the top level Activity.
     */
    public static GoogleCastSampleFragment newInstance(EventEmitter emitter, Context theContext) {
        GoogleCastSampleFragment googleCastSampleFragment = new GoogleCastSampleFragment();
        eventEmitter = emitter;
        context = theContext;
        return googleCastSampleFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Perform the internal wiring to be able to make use of the BrightcovePlayerFragment.
        View view = inflater.inflate(R.layout.basic_cast_fragment, container, false);
        brightcoveVideoView = (BrightcoveVideoView) view.findViewById(R.id.brightcove_video_view);
        brightcoveVideoView.setEventEmitter(eventEmitter);
        super.onCreateView(inflater, container, savedInstanceState);

        // Initialize the android_cast_plugin which requires the application id of your Cast
        // receiver application.
        String applicationId = getResources().getString(R.string.application_id);
        googleCastComponent = new GoogleCastComponent(eventEmitter, applicationId, context);

        // Initialize the MiniController widget which will allow control of remote media playback.
        miniController = (MiniController) view.findViewById(R.id.miniController1);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(GoogleCastComponent.CAST_MINICONTROLLER, miniController);
        eventEmitter.emit(GoogleCastEventType.SET_MINI_CONTROLLER, properties);

        // Send the location of the media (url) and its metadata information for remote playback.
        String url = getResources().getString(R.string.media_url);
        String imageUrl = getResources().getString(R.string.media_image);
        eventEmitter.emit(GoogleCastEventType.SET_MEDIA_METADATA,
                buildMetadataProperties("subTitle", "title", "studio", imageUrl, imageUrl, url));

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

    /**
     * Handle resuming Chromecast notifications on a resume lifecycle event.
     */
    @Override
    public void onResume() {
        super.onResume();
        eventEmitter.emit(GoogleCastEventType.SET_NOTIFICATIONS);
    }

    /**
     * Handle pausing Chromecast nofications on a pause lifecycle event.
     */
    @Override
    public void onPause() {
        super.onPause();
        eventEmitter.emit(GoogleCastEventType.UNSET_NOTIFICATIONS);
    }
}
