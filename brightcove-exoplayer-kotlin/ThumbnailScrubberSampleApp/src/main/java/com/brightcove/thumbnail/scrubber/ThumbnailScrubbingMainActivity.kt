package com.brightcove.thumbnail.scrubber

import android.os.Bundle
import android.util.Log
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.mediacontroller.ThumbnailComponent
import com.brightcove.player.model.Video
import com.brightcove.player.view.BaseVideoView
import com.brightcove.player.view.BrightcovePlayer
import com.brightcove.thumbnail.scrubber.databinding.ActivityThumbnailScrubbingMainBinding

class ThumbnailScrubbingMainActivity : BrightcovePlayer() {

    private lateinit var binding: ActivityThumbnailScrubbingMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        // When extending the BrightcovePlayer, we must assign brightcoveVideoView before
        // entering the superclass.  This allows for some stock video player lifecycle
        // management.
        binding = ActivityThumbnailScrubbingMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        brightcoveVideoView = binding.brightcoveVideoView
        configureThumbnailScrubber(brightcoveVideoView)

        super.onCreate(savedInstanceState)


        // Get the event emitter from the SDK and create a catalog request to fetch a video from the
        // Brightcove Edge service, given a video id, an account id and a policy key.
        val eventEmitter = brightcoveVideoView.eventEmitter

        val catalog = Catalog.Builder(eventEmitter, getString(R.string.account))
            .setPolicy(getString(R.string.policy))
            .build()


        // Retrieve the video, given a video id, an account id and a policy key
        catalog.findVideoByID(getString(R.string.videoId), object : VideoListener() {
            // Add the video found to the queue with add().
            // Start playback of the video with start().
            override fun onVideo(video: Video) {
                Log.v(TAG, "onVideo: video = $video")
                brightcoveVideoView.add(video)
                brightcoveVideoView.start()
            }
        })
    }

    /**
     * Configure the Thumbnail Scrubber using the ThumbnailComponent class
     * @param brightcoveVideoView       The VideoView object
     */
    private fun configureThumbnailScrubber(brightcoveVideoView: BaseVideoView) {
        Log.v(TAG,"Thumbnail Scrubbing is enabled, setting up the PreviewThumbnailController")
        val thumbnailComponent = ThumbnailComponent(brightcoveVideoView)
        thumbnailComponent.setupPreviewThumbnailController()
    }
}