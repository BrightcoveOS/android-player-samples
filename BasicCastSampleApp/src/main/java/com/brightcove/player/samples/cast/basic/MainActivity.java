package com.brightcove.player.samples.cast.basic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * This app illustrates how to use the Google Cast Plugin with the
 * Brightcove Player for Android.
 *
 * @author Billy Hnath (bhnath@brightcove.com)
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_cast);
    }
}