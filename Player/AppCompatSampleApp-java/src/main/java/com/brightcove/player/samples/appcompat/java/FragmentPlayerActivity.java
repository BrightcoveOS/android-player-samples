package com.brightcove.player.samples.appcompat.java;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Hosts the {@link PlayerFragment}, demonstrating the AppCompat plugin with a
 * BrightcovePlayerFragment.
 */
public class FragmentPlayerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
    }
}
