package com.brightcove.player.samples.cast.basic;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;

import com.brightcove.cast.GoogleCastComponent;
import com.brightcove.cast.GoogleCastEventType;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventEmitterImpl;

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

    public GoogleCastSampleFragment googleCastSampleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_cast);

        // We need a top level EventEmitter to handle communication to the ActionBar as well as
        // the underlying android-cast-plugin, so create a new one and pass it into the fragment.
        eventEmitter = new EventEmitterImpl();
        launchBrightcoveVideoViewFragment();
    }

    /**
     * Create an instance of the GoogleCastSampleFragment with an EventEmitter and a Context
     * pointing back to the main activity, then launch it.
     */
    public void launchBrightcoveVideoViewFragment() {
        Log.v(TAG, "launchBrightcoveVideoViewFragment:");
        googleCastSampleFragment = GoogleCastSampleFragment.newInstance(eventEmitter, this);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.brightcove_video_view_fragment, googleCastSampleFragment);
        fragmentTransaction.commit();
    }

    /**
     * When the actionbar menu is created, send an event off to the android_cast_plugin
     * to set up the Cast button.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            Log.v(TAG, "onCreateOptionsMenu:");
            super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.main, menu);

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(GoogleCastComponent.CAST_MENU, menu);
            properties.put(GoogleCastComponent.CAST_MENU_RESOURCE_ID, R.id.media_router_menu_item);
            eventEmitter.emit(GoogleCastEventType.SET_CAST_BUTTON, properties);
            return true;
    }
}