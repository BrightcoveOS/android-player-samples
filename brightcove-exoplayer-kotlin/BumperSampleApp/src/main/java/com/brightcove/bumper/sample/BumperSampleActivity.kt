package com.brightcove.bumper.sample

import android.os.Bundle
import android.util.Log
import com.brightcove.bumper.sample.databinding.ActivityBumperSampleAppBinding
import com.brightcove.player.bumper.BumperComponent
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.event.EventEmitter
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcovePlayer

class BumperSampleActivity : BrightcovePlayer() {

    private lateinit var binding: ActivityBumperSampleAppBinding

    private var bumperComponent: BumperComponent? = null

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
        val eventEmitter: EventEmitter = brightcoveVideoView.eventEmitter
        val account = getString(R.string.sdk_demo_account)

        val catalog = Catalog.Builder(eventEmitter, account)
            .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
            .setPolicy(getString(R.string.sdk_demo_policy))
            .build()


        //Building the instance of the bumper component, providing the existing videoView and catalog.
        bumperComponent = BumperComponent.Builder(brightcoveVideoView, catalog).build()

        //Initializing the bumper.
        bumperComponent?.init()

        catalog.findVideoByID(getString(R.string.sdk_demo_videoId), object : VideoListener() {
            // Add the video found to the queue with add().
            override fun onVideo(video: Video) {
                // Showcasing both options to set the bumper id manually or obtaining it from the
                // video object properties
                if (useSetBumperID) {
                    //Manually Setting our own bumper ID
                    bumperComponent?.setVideoBumperID(getString(R.string.sdk_demo_bumper_videoId))
                } else {
                    //Obtaining the bumper id from the video custom fields
                    val customFields =
                        video.properties[Video.Fields.CUSTOM_FIELDS] as Map<*, *>?
                    if ((customFields != null && customFields.isNotEmpty()) &&
                        (customFields.containsKey("bumper_id"))
                    ) {
                        bumperComponent?.setVideoBumperID(customFields["bumper_id"] as String?)
                    }
                }
                Log.v(TAG, "onVideo: video = $video")
                //Adding the video
                brightcoveVideoView.add(video)
                //Autostart Playback
                brightcoveVideoView.start()
            }
        })

    }
}