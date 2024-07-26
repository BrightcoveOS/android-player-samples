package com.brightcove.appcompatfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brightcove.appcompatfragment.databinding.FragmentAppCompatBinding
import com.brightcove.player.model.DeliveryType
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcovePlayerFragment

class AppCompatFragment: BrightcovePlayerFragment() {

    private lateinit var binding: FragmentAppCompatBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentAppCompatBinding.inflate(inflater,container,false)
        brightcoveVideoView = binding.brightcoveVideoView

        super.onCreateView(inflater, container, savedInstanceState)
        val video = Video.createVideo("https://media.w3.org/2010/05/sintel/trailer.mp4", DeliveryType.MP4)
        baseVideoView.add(video)
        baseVideoView.analytics.account = "1760897681001"
        baseVideoView.start()
        return binding.root
    }
}