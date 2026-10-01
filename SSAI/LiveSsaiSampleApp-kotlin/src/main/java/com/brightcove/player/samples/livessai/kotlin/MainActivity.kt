package com.brightcove.player.samples.livessai.kotlin

import android.os.Bundle
import android.util.Log
import com.brightcove.player.appcompat.BrightcovePlayerActivity
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.CatalogError
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.event.EventType
import com.brightcove.player.model.Video
import com.brightcove.player.network.HttpRequestConfig
import com.brightcove.player.samples.livessai.kotlin.databinding.ActivityLiveSsaiSampleBinding
import com.brightcove.ssai.SSAIComponent

/**
 * This app demonstrates server-side ad insertion (SSAI) on a NextGen Live 2.0 stream.
 */
class MainActivity : BrightcovePlayerActivity() {

    private lateinit var binding: ActivityLiveSsaiSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLiveSsaiSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        baseVideoView = binding.brightcoveVideoView
        super.onCreate(savedInstanceState)

        val eventEmitter = baseVideoView.eventEmitter
        eventEmitter.on(EventType.ERROR) { event -> Log.e(TAG, event.toString()) }

        val plugin = SSAIComponent(this, baseVideoView)
        val catalog = Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
            .setPolicy(getString(R.string.sdk_demo_policy))
            .build()

        // A NextGen Live 2.0 stream needs the live playback token in addition to the Ad Config ID.
        val httpRequestConfig = HttpRequestConfig.Builder()
            .addQueryParameter(HttpRequestConfig.KEY_AD_CONFIG_ID, getString(R.string.sdk_demo_ad_config_id))
            .addQueryParameter(HttpRequestConfig.KEY_LIVE_PLAYBACK_TOKEN, getString(R.string.sdk_demo_live_playback_token))
            .build()

        catalog.findVideoByID(getString(R.string.sdk_demo_video_id), httpRequestConfig, object : VideoListener() {
            override fun onVideo(video: Video) {
                plugin.processVideo(video)
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
