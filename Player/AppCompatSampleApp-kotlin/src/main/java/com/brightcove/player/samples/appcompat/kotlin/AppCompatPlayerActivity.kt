package com.brightcove.player.samples.appcompat.kotlin

import android.os.Bundle
import com.brightcove.player.samples.appcompat.kotlin.databinding.ActivityPlayerBinding
import com.brightcove.player.appcompat.BrightcovePlayerActivity
import com.brightcove.player.model.DeliveryType
import com.brightcove.player.model.Video

/**
 * Demonstrates the AppCompat plugin by extending BrightcovePlayerActivity.
 */
class AppCompatPlayerActivity : BrightcovePlayerActivity() {

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val video = Video.createVideo("https://media.w3.org/2010/05/sintel/trailer.mp4", DeliveryType.MP4)

        baseVideoView.apply {
            add(video)
            analytics.account = "1760897681001"
            start()
        }
    }
}
