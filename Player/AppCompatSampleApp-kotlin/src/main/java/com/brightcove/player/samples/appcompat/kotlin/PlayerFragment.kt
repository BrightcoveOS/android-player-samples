package com.brightcove.player.samples.appcompat.kotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brightcove.player.samples.appcompat.kotlin.databinding.FragmentAppCompatBinding
import com.brightcove.player.model.DeliveryType
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcovePlayerFragment

/**
 * BrightcovePlayerFragment that loads and plays the sample video.
 */
class PlayerFragment : BrightcovePlayerFragment() {

    private lateinit var binding: FragmentAppCompatBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentAppCompatBinding.inflate(inflater, container, false)
        brightcoveVideoView = binding.brightcoveVideoView

        super.onCreateView(inflater, container, savedInstanceState)
        val video = Video.createVideo(getString(R.string.sdk_demo_video_url), DeliveryType.MP4)
        baseVideoView.add(video)
        baseVideoView.analytics.account = getString(R.string.sdk_demo_account)
        baseVideoView.start()
        return binding.root
    }
}
