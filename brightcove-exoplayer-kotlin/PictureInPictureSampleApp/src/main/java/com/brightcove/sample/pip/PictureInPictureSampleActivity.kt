package com.brightcove.sample.pip

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.model.Video
import com.brightcove.player.pictureinpicture.PictureInPictureManager
import com.brightcove.player.view.BrightcovePlayer
import com.brightcove.sample.pip.databinding.ActivityPictureInPictureSampleBinding

class PictureInPictureSampleActivity : BrightcovePlayer() {

    private lateinit var binding: ActivityPictureInPictureSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPictureInPictureSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        brightcoveVideoView = binding.brightcoveVideoView
        super.onCreate(savedInstanceState)

        // Get the event emitter from the SDK and create a catalog request to fetch a video from the
        // Brightcove Edge service, given a video id, an account id and a policy key.
        val eventEmitter = brightcoveVideoView.eventEmitter
        val catalog = Catalog.Builder(eventEmitter, getString(R.string.account))
            .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
            .setPolicy(getString(R.string.policy))
            .build()

        catalog.findVideoByID(getString(R.string.videoId), object : VideoListener() {
            // Add the video found to the queue with add().
            // Start playback of the video with start().
            override fun onVideo(video: Video) {
                Log.v(TAG, "onVideo: video = $video")
                brightcoveVideoView.add(video)
                brightcoveVideoView.start()
            }
        })

        binding.configurePip.setOnClickListener {
            onClickConfigurePictureInPicture()
        }
    }

    override fun onResume() {
        super.onResume()
        val settingsModel = SettingsModel(this)

        //Configure Picture in Picture
        val manager = PictureInPictureManager.getInstance()
        manager.setClosedCaptionsEnabled(settingsModel.isPictureInPictureClosedCaptionsEnabled())
            .setOnUserLeaveEnabled(settingsModel.isPictureInPictureOnUserLeaveEnabled())
            .setClosedCaptionsReductionScaleFactor(settingsModel.getPictureInPictureCCScaleFactor())
            .setAspectRatio(settingsModel.getPictureInPictureAspectRatio())
    }

    private fun onClickConfigurePictureInPicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(
                this,
                "Picture-in-Picture is currently available only on Android Oreo or Higher",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
