package com.brightcove.basic.sample

import android.os.Bundle
import android.util.Log
import com.brightcove.basic.sample.databinding.ActivityBasicSampleAppBinding
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.event.EventEmitter
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcovePlayer

class BasicSampleActivity : BrightcovePlayer() {

    private lateinit var binding: ActivityBasicSampleAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        // When extending the BrightcovePlayer, we must assign brightcoveVideoView before
        // entering the superclass.  This allows for some stock video player lifecycle
        // management.
        binding = ActivityBasicSampleAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        brightcoveVideoView = binding.brightcoveVideoView
        super.onCreate(savedInstanceState)


        // Get the event emitter from the SDK and create a catalog request to fetch a video from the
        // Brightcove Edge service, given a video id, an account id and a policy key.
        val eventEmitter: EventEmitter = brightcoveVideoView.eventEmitter
        val account = getString(R.string.sdk_demo_account)

        val catalog = Catalog.Builder(eventEmitter, account)
            .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
            .setPolicy(getString(R.string.sdk_demo_policy))
            .build()

        catalog.findVideoByID(getString(R.string.sdk_demo_videoId), object : VideoListener() {
            // Add the video found to the queue with add().
            // Start playback of the video with start().
            override fun onVideo(video: Video) {
                Log.v(TAG, "onVideo: video = $video")
                brightcoveVideoView.add(video)
                brightcoveVideoView.start()
            }
        })

    }
}