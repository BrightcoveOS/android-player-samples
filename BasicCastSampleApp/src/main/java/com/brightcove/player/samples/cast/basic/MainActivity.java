package com.brightcove.player.samples.cast.basic;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.widget.MediaController;

import com.brightcove.cast.GoogleCastComponent;
import com.brightcove.cast.GoogleCastComponentConstants;
import com.brightcove.cast.GoogleCastEventType;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventEmitterImpl;
import com.brightcove.player.view.BrightcoveVideoView;
import com.google.sample.castcompanionlibrary.widgets.MiniController;

import java.util.HashMap;
import java.util.Map;

/**
 * This app illustrates how to use the Google Cast Plugin with the
 * Brightcove Player for Android.
 *
 * @author Billy Hnath (bhnath@brightcove.com)
 */
public class MainActivity extends ActionBarActivity {
    //Need to extend BrightcovePlayer and ActionBarActivity
    //Interface to BrightcovePlayer?
    //Might be a good way to demo not extending off of BrightcovePlayer
    //for customers that need to extend off something else instead.

    // use a fragment!

    public static final String TAG = MainActivity.class.getSimpleName();

    private EventEmitter eventEmitter;
    private GoogleCastComponent googleCastComponent;
    //private BrightcovePlayer brightcovePlayer;
    private BrightcoveVideoView brightcoveVideoView;
    private MiniController miniController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_cast);
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView
        // before entering the superclass. This allows for some stock video player lifecycle
        // management.
        //brightcovePlayer = new BrightcovePlayer();
        eventEmitter = new EventEmitterImpl();
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.bc_video_view);
        //eventEmitter = brightcoveVideoView.getEventEmitter();

        brightcoveVideoView.setEventEmitter(eventEmitter);
        brightcoveVideoView.setMediaController(new MediaController(this));

        String applicationId = getResources().getString(R.string.application_id);
        googleCastComponent = new GoogleCastComponent(eventEmitter, applicationId, this);

        miniController = (MiniController) findViewById(R.id.miniController1);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(GoogleCastComponentConstants.CAST_MINICONTROLLER, miniController);
        eventEmitter.emit(GoogleCastEventType.SET_MINI_CONTROLLER, properties);

        String url = getResources().getString(R.string.media_url);
        String bbimg = getResources().getString(R.string.media_image);
        eventEmitter.emit(GoogleCastEventType.SET_MEDIA_METADATA,
            buildMetadataProperties("subTitle", "title", "studio", bbimg, bbimg, url));

        brightcoveVideoView.setVideoPath(url);
    }

    private Map<String, Object> buildMetadataProperties(String subTitle, String title, String studio, String imageUrl, String bigImageUrl, String url) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(GoogleCastComponentConstants.CAST_MEDIA_METADATA_SUBTITLE, subTitle);
        properties.put(GoogleCastComponentConstants.CAST_MEDIA_METADATA_TITLE, title);
        properties.put(GoogleCastComponentConstants.CAST_MEDIA_METADATA_STUDIO, studio);
        properties.put(GoogleCastComponentConstants.CAST_MEDIA_METADATA_IMAGE_URL, imageUrl);
        properties.put(GoogleCastComponentConstants.CAST_MEDIA_METADATA_BIG_IMAGE_URL, bigImageUrl);
        properties.put(GoogleCastComponentConstants.CAST_MEDIA_METADATA_URL, url);
        return properties;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.main, menu);

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(GoogleCastComponentConstants.CAST_MENU, menu);
            properties.put(GoogleCastComponentConstants.CAST_MENU_RESOURCE_ID, R.id.media_router_menu_item);
            eventEmitter.emit(GoogleCastEventType.SET_CAST_BUTTON, properties);
            return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        eventEmitter.emit(GoogleCastEventType.SET_NOTIFICATIONS);
    }

    @Override
    public void onPause() {
        super.onPause();
        eventEmitter.emit(GoogleCastEventType.UNSET_NOTIFICATIONS);
    }
}