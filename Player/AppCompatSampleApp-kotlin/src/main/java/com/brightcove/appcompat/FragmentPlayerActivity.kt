package com.brightcove.appcompat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Hosts the [PlayerFragment], demonstrating the AppCompat plugin with a BrightcovePlayerFragment.
 */
class FragmentPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
    }
}
