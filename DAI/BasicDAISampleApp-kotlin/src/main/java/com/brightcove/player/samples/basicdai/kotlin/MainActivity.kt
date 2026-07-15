package com.brightcove.player.samples.basicdai.kotlin

import android.os.Bundle
import android.util.Log
import com.brightcove.dai.GoogleDAIComponent
import com.brightcove.player.samples.basicdai.kotlin.databinding.ActivityMainBinding
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.CatalogError
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.event.EventEmitter
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcovePlayer
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory

/**
 * Plays a stream with Google Dynamic Ad Insertion (DAI), where ads are
 * stitched into the stream server-side.
 */
class MainActivity : BrightcovePlayer() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var eventEmitter: EventEmitter
    private lateinit var catalog: Catalog
    private lateinit var googleDAIComponent: GoogleDAIComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        brightcoveVideoView = binding.brightcoveVideoView
        super.onCreate(savedInstanceState)
        eventEmitter = brightcoveVideoView.eventEmitter
        setupDAI()
        catalog = Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
            .setPolicy(getString(R.string.sdk_demo_policy))
            .build()
        requestVideo()
    }

    private fun setupDAI() {
        val imaSdkSettings = ImaSdkFactory.getInstance().createImaSdkSettings()
        val daiBuilder = GoogleDAIComponent.Builder(brightcoveVideoView, eventEmitter)
            .setImaSdkSettings(imaSdkSettings)
        googleDAIComponent = daiBuilder.build()
    }

    private fun requestVideo() {
        catalog.findVideoByID(getString(R.string.sdk_demo_video_id), object : VideoListener() {
            override fun onVideo(video: Video) {
                // Provide a fallback video to play if the DAI stream cannot be retrieved,
                // then register a callback that receives the ad-stitched stream once it is ready.
                googleDAIComponent.setFallbackVideo(video)
                googleDAIComponent.addCallback(object : GoogleDAIComponent.Listener {
                    override fun onStreamReady(video: Video) {
                        Log.d(TAG, "onStreamReady")
                        brightcoveVideoView.add(video)
                        brightcoveVideoView.start()
                    }

                    override fun onContentComplete() {
                        Log.d(TAG, "onContentComplete")
                    }
                })

                // This sample requests a VOD stream. To request a live stream instead,
                // uncomment the line below and comment out the requestVOD call.
                //googleDAIComponent.requestLiveStream(LIVE_BIG_BUCK_BUNNY_ASSET_KEY, null)
                googleDAIComponent.requestVOD(VOD_TEARS_OF_STEEL_CMS_ID, VOD_TEARS_OF_STEEL_VIDEO_ID, null)
            }

            override fun onError(errors: List<CatalogError>) {
                Log.e(TAG, errors.toString())
            }
        })
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val LIVE_BIG_BUCK_BUNNY_ASSET_KEY = "c-rArva4ShKVIAkNfy6HUQ"
        private const val VOD_TEARS_OF_STEEL_CMS_ID = "2548831"
        private const val VOD_TEARS_OF_STEEL_VIDEO_ID = "tears-of-steel"
    }
}