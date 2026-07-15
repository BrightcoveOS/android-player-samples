package com.brightcove.player.samples.seekbarcolors.kotlin

import android.os.Bundle
import android.util.Log
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.CatalogError
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcovePlayer

/**
 * This app illustrates recoloring the Android default media controller's seek bar. The
 * customization is entirely in res/values/colors.xml (the bmc_seekbar_* colors).
 */
class MainActivity : BrightcovePlayer() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // When extending the BrightcovePlayer, we must assign brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle management.
        setContentView(R.layout.default_activity_main)
        brightcoveVideoView = findViewById(R.id.brightcove_video_view)
        super.onCreate(savedInstanceState)

        val catalog = Catalog.Builder(brightcoveVideoView.eventEmitter, getString(R.string.sdk_demo_account))
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

    companion object {
        private const val TAG = "MainActivity"
    }
}
