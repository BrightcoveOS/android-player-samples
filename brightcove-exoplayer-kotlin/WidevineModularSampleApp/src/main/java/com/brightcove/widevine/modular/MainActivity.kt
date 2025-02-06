package com.brightcove.widevine.modular

import android.os.Bundle
import android.view.View
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcoveExoPlayerVideoView
import com.brightcove.player.view.BrightcovePlayer

class MainActivity : BrightcovePlayer() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main)
        brightcoveVideoView =
            findViewById<View>(R.id.brightcove_video_view) as BrightcoveExoPlayerVideoView
        super.onCreate(savedInstanceState)

        val catalog =
            Catalog.Builder(brightcoveVideoView.eventEmitter, getString(R.string.sdk_demo_account))
                .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
                .setPolicy(getString(R.string.sdk_demo_policy))
                .build()

        catalog.findVideoByID(getString(R.string.sdk_demo_videoId), object : VideoListener() {
            override fun onVideo(video: Video) {
                brightcoveVideoView.add(video)
                brightcoveVideoView.start()
            }
        })
    }
}