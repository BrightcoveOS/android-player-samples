package com.brightcove.appcompat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.brightcove.appcompat.databinding.ActivityMainBinding

/**
 * Launcher that lets you choose between the two ways to integrate the Brightcove player with the
 * AppCompat plugin: a BrightcovePlayerActivity (AppCompatPlayerActivity) or a
 * BrightcovePlayerFragment hosted in an AppCompatActivity (FragmentPlayerActivity).
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activityExampleButton.setOnClickListener {
            startActivity(Intent(this, AppCompatPlayerActivity::class.java))
        }
        binding.fragmentExampleButton.setOnClickListener {
            startActivity(Intent(this, FragmentPlayerActivity::class.java))
        }
    }
}
