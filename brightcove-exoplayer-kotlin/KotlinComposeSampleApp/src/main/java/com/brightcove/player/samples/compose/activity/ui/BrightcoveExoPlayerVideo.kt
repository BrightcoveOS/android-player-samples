package com.brightcove.player.samples.compose.activity.ui

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.brightcove.player.model.DeliveryType.MP4
import com.brightcove.player.model.Video
import com.brightcove.player.view.BaseVideoView
import com.brightcove.player.view.BrightcoveExoPlayerVideoView

@Composable
fun BrightcoveExoPlayerVideo(
    modifier: Modifier = Modifier.fillMaxSize(),
    initializeLifecycleUtil: (baseVideoView: BaseVideoView) -> Unit
) {
    AndroidView(
        modifier = modifier,
        // The viewBlock provides us with the Context so we do not have to pass this down into the @Composable
        // ourself
        factory = { context ->
            val baseVideoView = BrightcoveExoPlayerVideoView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }
            baseVideoView.eventEmitter
            initializeLifecycleUtil(baseVideoView)
            val video =
                Video.createVideo("https://media.w3.org/2010/05/sintel/trailer.mp4", MP4)
            baseVideoView.apply {
                add(video)
                analytics.account = "1760897681001"
                start()
            }
            baseVideoView
        })
}