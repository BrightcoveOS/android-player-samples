package com.brightcove.player.samples.appcompat.java;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Launcher that lets you choose between the two ways to integrate the Brightcove player with the
 * AppCompat plugin: a BrightcovePlayerActivity (AppCompatPlayerActivity) or a
 * BrightcovePlayerFragment hosted in an AppCompatActivity (FragmentPlayerActivity).
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.activity_example_button).setOnClickListener(v ->
                startActivity(new Intent(this, AppCompatPlayerActivity.class)));
        findViewById(R.id.fragment_example_button).setOnClickListener(v ->
                startActivity(new Intent(this, FragmentPlayerActivity.class)));
    }
}
