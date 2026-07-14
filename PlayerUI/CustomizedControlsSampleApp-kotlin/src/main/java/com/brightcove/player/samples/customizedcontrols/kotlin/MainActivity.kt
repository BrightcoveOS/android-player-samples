package com.brightcove.player.samples.customizedcontrols.kotlin

import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.event.EventType
import com.brightcove.player.mediacontroller.BrightcoveMediaController
import com.brightcove.player.model.Video
import com.brightcove.player.view.BaseVideoView
import com.brightcove.player.view.BrightcoveExoPlayerVideoView
import com.brightcove.player.view.BrightcovePlayer

/**
 * This app illustrates how to customize the Android default media controller: it swaps in a
 * custom media-controller layout (with an Android TV variant) and wires up a Font Awesome
 * "thumbs up" button.
 */
class MainActivity : BrightcovePlayer() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // When extending the BrightcovePlayer, we must assign brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle management.
        setContentView(R.layout.default_activity_main)
        brightcoveVideoView = findViewById<BrightcoveExoPlayerVideoView>(R.id.brightcove_video_view)
        initMediaController(brightcoveVideoView)
        super.onCreate(savedInstanceState)

        val catalog = Catalog.Builder(brightcoveVideoView.eventEmitter, getString(R.string.sdk_demo_account))
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

    private fun initMediaController(videoView: BaseVideoView) {
        // Use a dedicated layout when running on Android TV.
        val layout = if (BrightcoveMediaController.checkTvMode(this)) {
            R.layout.my_tv_media_controller
        } else {
            R.layout.my_media_controller
        }
        videoView.setMediaController(BrightcoveMediaController(videoView, layout))
        initButtons(videoView)

        // Sent by the BrightcovePlayer Activity when onConfigurationChanged has been called.
        videoView.eventEmitter.on(EventType.CONFIGURATION_CHANGED) { initButtons(videoView) }
    }

    private fun initButtons(videoView: BaseVideoView) {
        val font = Typeface.createFromAsset(assets, FONT_AWESOME)
        val thumbsUp = videoView.findViewById<Button>(R.id.thumbs_up)
        // Setting this typeface lets us use the icons in the Font Awesome file.
        thumbsUp?.typeface = font
        thumbsUp?.setOnClickListener {
            Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        // This TTF font is included in the Brightcove SDK.
        private const val FONT_AWESOME = "fontawesome-webfont.ttf"
    }
}
