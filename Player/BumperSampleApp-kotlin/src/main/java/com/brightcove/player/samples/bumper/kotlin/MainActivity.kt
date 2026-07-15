package com.brightcove.player.samples.bumper.kotlin

import android.os.Bundle
import android.util.Log
import com.brightcove.player.samples.bumper.kotlin.databinding.ActivityBumperSampleAppBinding
import com.brightcove.player.bumper.BumperComponent
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.CatalogError
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcovePlayer

/**
 * This app illustrates how to use the ExoPlayer and the BumperComponent with the Brightcove
 * Native Player SDK for Android.
 */
class MainActivity : BrightcovePlayer() {

    private lateinit var binding: ActivityBumperSampleAppBinding

    private var bumperComponent: BumperComponent? = null

    // Demo switch: when true the bumper id is set manually; flip to false to read it from the
    // video's "bumper_id" custom field instead.
    private val useSetBumperID = true

    override fun onCreate(savedInstanceState: Bundle?) {

        // When extending the BrightcovePlayer, we must assign brightcoveVideoView before
        // entering the superclass.  This allows for some stock video player lifecycle
        // management.
        binding = ActivityBumperSampleAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        brightcoveVideoView = binding.brightcoveVideoView
        super.onCreate(savedInstanceState)

        // Get the event emitter from the SDK and create a catalog request to fetch a video from the
        // Brightcove Edge service, given a video id, an account id and a policy key.
        val eventEmitter = brightcoveVideoView.eventEmitter
        val account = getString(R.string.sdk_demo_account)

        val catalog = Catalog.Builder(eventEmitter, account)
            .setPolicy(getString(R.string.sdk_demo_policy))
            .build()

        // Build the bumper component with the existing videoView and catalog.
        bumperComponent = BumperComponent.Builder(brightcoveVideoView, catalog).build()
        // Initialize the bumper.
        bumperComponent?.init()

        catalog.findVideoByID(getString(R.string.sdk_demo_video_id), object : VideoListener() {
            // Add the video found to the queue with add().
            override fun onVideo(video: Video) {
                // Showcasing both options to set the bumper id manually or obtaining it from the
                // video object properties
                if (useSetBumperID) {
                    // Manually set our own bumper id.
                    bumperComponent?.setVideoBumperID(getString(R.string.sdk_demo_bumper_videoId))
                } else {
                    // Obtain the bumper id from the video's custom fields.
                    val customFields =
                        video.properties[Video.Fields.CUSTOM_FIELDS] as Map<*, *>?
                    if ((customFields != null && customFields.isNotEmpty()) &&
                        (customFields.containsKey("bumper_id"))
                    ) {
                        bumperComponent?.setVideoBumperID(customFields["bumper_id"] as String?)
                    }
                }
                Log.v(TAG, "onVideo: video = $video")
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