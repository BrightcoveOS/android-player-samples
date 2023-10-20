package com.brightcove.live.sample

import android.os.Bundle
import com.brightcove.live.sample.databinding.ActivityLiveSampleBinding
import com.brightcove.player.model.DeliveryType
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcovePlayer

class LiveSampleActivity : BrightcovePlayer() {

    private lateinit var binding: ActivityLiveSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        binding = ActivityLiveSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        brightcoveVideoView = binding.brightcoveVideoView
        super.onCreate(savedInstanceState)

        val video = Video.createVideo("YOUR_LIVE_HLS_STREAM", DeliveryType.HLS)
        video.properties[Video.Fields.PUBLISHER_ID] = "YOUR_VIDEOCLOUD_PUBLISHER_ID"
        brightcoveVideoView.add(video)
        brightcoveVideoView.start()
    }
}
