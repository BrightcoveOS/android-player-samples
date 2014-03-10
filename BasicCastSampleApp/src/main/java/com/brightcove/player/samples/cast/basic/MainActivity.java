package com.brightcove.player.samples.cast.basic;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_cast);

        BrightcoveVideoViewFragment brightcoveVideoViewFragment = new BrightcoveVideoViewFragment();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.brightcove_video_view_fragment, brightcoveVideoViewFragment);
        fragmentTransaction.commit();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//            super.onCreateOptionsMenu(menu);
//            getMenuInflater().inflate(R.menu.main, menu);
//
//            Map<String, Object> properties = new HashMap<String, Object>();
//            properties.put(GoogleCastComponentConstants.CAST_MENU, menu);
//            properties.put(GoogleCastComponentConstants.CAST_MENU_RESOURCE_ID, R.id.media_router_menu_item);
//            eventEmitter.emit(GoogleCastEventType.SET_CAST_BUTTON, properties);
//            return true;
//    }

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