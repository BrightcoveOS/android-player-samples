package com.brightcove.player.samples.appcompat;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Hosts the {@link PlayerFragment}, demonstrating the AppCompat plugin with a
 * BrightcovePlayerFragment.
 */
public class FragmentPlayerActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
    }
}
