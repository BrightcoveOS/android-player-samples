package com.brightcove.player.samples.video360.kotlin

import android.os.Bundle
import android.util.Log
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcovePlayer
import com.brightcove.player.samples.video360.kotlin.databinding.ActivityMainBinding

/**
 * This app illustrates how to play a 360 (equirectangular) video with the Brightcove
 * Native Player SDK for Android.
 */
class Video360Activity : BrightcovePlayer() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // When extending the BrightcovePlayer, we must assign brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle management.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        brightcoveVideoView = binding.brightcoveVideoView
        super.onCreate(savedInstanceState)

        val catalog = Catalog.Builder(brightcoveVideoView.eventEmitter, getString(R.string.account))
            .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
            .setPolicy(getString(R.string.policy))
            .build()

        catalog.findVideoByID(getString(R.string.videoId), object : VideoListener() {
            override fun onVideo(video: Video) {
                if (video.projectionFormat == Video.ProjectionFormat.EQUIRECTANGULAR) {
                    Log.i(TAG, "This is a 360 video")
                }
                brightcoveVideoView.add(video)
                brightcoveVideoView.start()
            }
        })
        // You can also create a 360 video by setting the projection format on creation:
        // val video = Video.createVideo(VIDEO_URL, VIDEO_TYPE, PROJECTION_FORMAT)
    }
}
