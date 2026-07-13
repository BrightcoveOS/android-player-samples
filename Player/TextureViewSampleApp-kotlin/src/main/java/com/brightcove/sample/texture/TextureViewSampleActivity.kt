package com.brightcove.sample.texture

import android.os.Bundle
import android.util.Log
import android.view.View
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcoveExoPlayerTextureVideoView
import com.brightcove.player.view.BrightcovePlayer
import com.brightcove.sample.texture.databinding.ActivityTextureViewSampleBinding

class TextureViewSampleActivity : BrightcovePlayer() {

    private lateinit var binding: ActivityTextureViewSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        binding = ActivityTextureViewSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        brightcoveVideoView = findViewById<View>(R.id.brightcove_video_view) as BrightcoveExoPlayerTextureVideoView
        super.onCreate(savedInstanceState)

        // Get the event emitter from the SDK and create a catalog request to fetch a video from the
        // Brightcove Edge service, given a video id, an account id and a policy key.
        val eventEmitter = brightcoveVideoView.eventEmitter
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

    companion object {
        private val TAG = TextureViewSampleActivity::class.java.simpleName
    }
}
