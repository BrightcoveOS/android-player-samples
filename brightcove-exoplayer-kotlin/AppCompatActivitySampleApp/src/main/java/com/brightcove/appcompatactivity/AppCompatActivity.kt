package com.brightcove.appcompatactivity

import android.os.Bundle
import com.brightcove.appcompatactivity.databinding.ActivityAppCompatBinding
import com.brightcove.player.appcompat.BrightcovePlayerActivity
import com.brightcove.player.model.DeliveryType
import com.brightcove.player.model.Video

/**
 * Basic example of how to use the appcompat plugin.
 */

class AppCompatActivity : BrightcovePlayerActivity() {

    private lateinit var binding : ActivityAppCompatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAppCompatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val video = Video.createVideo("https://media.w3.org/2010/05/sintel/trailer.mp4", DeliveryType.MP4)

        baseVideoView.apply {
            add(video)
            analytics.account = "1760897681001"
            start()
        }
    }
}