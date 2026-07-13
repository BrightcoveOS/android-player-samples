package com.brightcove.player.samples.styledcontrols.kotlin

import android.os.Bundle
import android.util.Log
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcoveExoPlayerVideoView
import com.brightcove.player.view.BrightcovePlayer

/**
 * This app illustrates how to style the Android default media controller. The styling lives in
 * res/values/styles.xml plus a custom scrubber drawable.
 */
class MainActivity : BrightcovePlayer() {

    private val tag = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        // When extending the BrightcovePlayer, we must assign brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle management.
        setContentView(R.layout.default_activity_main)
        brightcoveVideoView = findViewById<BrightcoveExoPlayerVideoView>(R.id.brightcove_video_view)
        super.onCreate(savedInstanceState)

        val catalog = Catalog.Builder(brightcoveVideoView.eventEmitter, getString(R.string.sdk_demo_account))
            .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
            .setPolicy(getString(R.string.sdk_demo_policy))
            .build()

        catalog.findVideoByID(getString(R.string.sdk_demo_videoId), object : VideoListener() {
            override fun onVideo(video: Video) {
                Log.v(tag, "onVideo: video = $video")
                brightcoveVideoView.add(video)
                brightcoveVideoView.start()
            }
        })
    }
}
