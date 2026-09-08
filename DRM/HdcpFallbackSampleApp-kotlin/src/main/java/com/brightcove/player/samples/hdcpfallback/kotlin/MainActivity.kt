package com.brightcove.player.samples.hdcpfallback.kotlin

import android.os.Bundle
import android.util.Log
import androidx.media3.common.Format
import com.brightcove.player.display.ExoPlayerVideoDisplayComponent
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.CatalogError
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.model.Video
import com.brightcove.player.samples.hdcpfallback.kotlin.databinding.ActivityMainBinding
import com.brightcove.player.view.BrightcovePlayer

/**
 * This app illustrates HDCP fallback with the Brightcove Native Player SDK for Android.
 *
 * The video comes from an account that assigns a DRM key per rendition tier: the SD key plays on any
 * output, the HD key only plays over an HDCP-protected one. [HdcpFallback] reads the HDCP state of
 * the connected output and restricts rendition selection to SD while that output is unprotected, so
 * both classes of device play the video: a protected output adapts up to HD, and an unprotected one
 * stays on SD instead of failing with a decrypt error.
 */
class MainActivity : BrightcovePlayer() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var hdcpFallback: HdcpFallback

    override fun onCreate(savedInstanceState: Bundle?) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use its event emitter to get important
        // notifications and to control logging.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        brightcoveVideoView = binding.brightcoveVideoView
        super.onCreate(savedInstanceState)

        hdcpFallback = HdcpFallback(brightcoveVideoView)

        logSelectedRendition()

        val catalog =
            Catalog.Builder(brightcoveVideoView.eventEmitter, getString(R.string.sdk_demo_account))
                .setPolicy(getString(R.string.sdk_demo_policy))
                .build()

        catalog.findVideoByID(getString(R.string.sdk_demo_video_id), object : VideoListener() {
            override fun onVideo(video: Video) {
                brightcoveVideoView.add(video)
                brightcoveVideoView.start()
            }

            override fun onError(errors: List<CatalogError>) {
                Log.e(TAG, errors.toString())
            }
        })
    }

    override fun onDestroy() {
        hdcpFallback.release()
        super.onDestroy()
    }

    /** Reports every rendition change, so that the effect of the HDCP policy is visible in logcat. */
    private fun logSelectedRendition() {
        brightcoveVideoView.eventEmitter.on(ExoPlayerVideoDisplayComponent.RENDITION_CHANGED) { event ->
            val format = event.getProperty(
                ExoPlayerVideoDisplayComponent.EXOPLAYER_FORMAT,
                Format::class.java
            )
            if (format != null && format.height > 0) {
                Log.i(TAG, "Playing rendition ${format.width}x${format.height} at ${format.bitrate} bps")
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
