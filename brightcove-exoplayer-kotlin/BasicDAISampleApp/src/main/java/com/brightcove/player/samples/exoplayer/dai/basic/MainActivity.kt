package com.brightcove.player.samples.exoplayer.dai.basic

import android.os.Bundle
import android.util.Log
import com.brightcove.dai.GoogleDAIComponent
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.event.EventEmitter
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcovePlayer
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory

class MainActivity : BrightcovePlayer() {
    private val TAG = this.javaClass.simpleName
    private lateinit var eventEmitter: EventEmitter
    private lateinit var catalog: Catalog
    private lateinit var googleDAIComponent: GoogleDAIComponent

    public override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        brightcoveVideoView = findViewById(R.id.brightcove_video_view)
        super.onCreate(savedInstanceState)
        eventEmitter = brightcoveVideoView.eventEmitter
        setupDAI()
        catalog = Catalog.Builder(eventEmitter, getString(R.string.account))
            .setPolicy(getString(R.string.policy))
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
        catalog.findVideoByID(getString(R.string.videoId), object : VideoListener() {
            override fun onVideo(video: Video) {
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

                // Uncomment the next line if you want to request a live stream
                //googleDAIComponent.requestLiveStream(LIVE_BIG_BUCK_BUNNY_ASSET_KEY, null);

                // Uncomment the next line if you want to request a VOD
                googleDAIComponent.requestVOD(VOD_TEARS_OF_STEEL_CMS_ID, VOD_TEARS_OF_STEEL_VIDEO_ID, null)
            }
        })
    }

    companion object {
        private const val LIVE_BIG_BUCK_BUNNY_ASSET_KEY = "c-rArva4ShKVIAkNfy6HUQ"
        private const val VOD_TEARS_OF_STEEL_CMS_ID = "2528370"
        private const val VOD_TEARS_OF_STEEL_VIDEO_ID = "tears-of-steel"
    }
}