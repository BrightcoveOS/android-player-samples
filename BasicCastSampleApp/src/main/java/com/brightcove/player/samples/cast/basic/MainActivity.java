package com.brightcove.player.samples.cast.basic;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.brightcove.cast.GoogleCastComponent;
import com.brightcove.cast.GoogleCastEventType;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventEmitterImpl;
import com.brightcove.player.view.BrightcoveVideoView;

import java.util.HashMap;
import java.util.Map;

/**
 * This app illustrates how to use the Google Cast Plugin with the
 * Brightcove Player for Android.
 *
 * @author Billy Hnath (bhnath@brightcove.com)
 */
public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private EventEmitter eventEmitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_cast);

        eventEmitter = new EventEmitterImpl();

        BrightcoveVideoViewFragment brightcoveVideoViewFragment = BrightcoveVideoViewFragment.newInstance(eventEmitter);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.brightcove_video_view_fragment, brightcoveVideoViewFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.main, menu);

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(GoogleCastComponent.CAST_MENU, menu);
            properties.put(GoogleCastComponent.CAST_MENU_RESOURCE_ID, R.id.media_router_menu_item);
            eventEmitter.emit(GoogleCastEventType.SET_CAST_BUTTON, properties);
            return true;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        eventEmitter.emit(GoogleCastEventType.SET_NOTIFICATIONS);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        eventEmitter.emit(GoogleCastEventType.UNSET_NOTIFICATIONS);
//    }
}