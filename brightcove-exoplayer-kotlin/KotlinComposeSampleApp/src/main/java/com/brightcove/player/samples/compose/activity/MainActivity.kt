package com.brightcove.player.samples.compose.activity

import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_ANY
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleEventObserver
import com.brightcove.player.event.EventEmitterImpl
import com.brightcove.player.event.EventLogger
import com.brightcove.player.model.DeliveryType.MP4
import com.brightcove.player.model.Video
import com.brightcove.player.playback.PlaybackNotification
import com.brightcove.player.samples.compose.activity.ui.theme.AndroidplayersamplesTheme
import com.brightcove.player.util.LifecycleUtil
import com.brightcove.player.view.BaseVideoView
import com.brightcove.player.view.BrightcoveExoPlayerVideoView

class MainActivity : ComponentActivity(), PlaybackNotification.OnRestorePlaybackHandler {

    val TAG: String = MainActivity::class.java.simpleName
    lateinit var baseVideoView: BaseVideoView
    //lateinit var eventLogger: EventLogger
    lateinit var lifecycleUtil: LifecycleUtil
    //private var savedInstanceState: Bundle? = null

    fun showClosedCaptioningDialog() {
        baseVideoView.closedCaptioningController?.showCaptionsDialog()
    }

    fun enterFullScreen() {
        baseVideoView.eventEmitter?.emit("enterFullScreen")
    }

    fun exitFullScreen() {
        baseVideoView.eventEmitter?.emit("exitFullScreen")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AndroidplayersamplesTheme {

                var lifecycle by remember {
                    mutableStateOf(Lifecycle.Event.ON_CREATE)
                }

                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        lifecycle = event
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)

                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9f),
                        // The viewBlock provides us with the Context so we do not have to pass this down into the @Composable
                        // ourself
                        factory = { context ->
                            val baseVideoView = BrightcoveExoPlayerVideoView(context).apply {
                                layoutParams = LayoutParams(
                                    LayoutParams.MATCH_PARENT,
                                    LayoutParams.MATCH_PARENT,
                                )
                            }
                            baseVideoView.eventEmitter = EventEmitterImpl()
                            //*****
                            lifecycleUtil = LifecycleUtil(baseVideoView)
                            lifecycleUtil.onCreate(savedInstanceState, this)
                            //eventLogger =
                            //    EventLogger(this.baseVideoView.eventEmitter,
                            //        true, this.javaClass.simpleName)
                            ///*****
                            val video =
                                Video.createVideo("https://media.w3.org/2010/05/sintel/trailer.mp4", MP4)
                            baseVideoView.apply {
                                add(video)
                                analytics.account = "1760897681001"
                                start()
                            }
                            baseVideoView
                        },
                        update = {
                            when (lifecycle) {
                                ON_PAUSE -> {
                                    lifecycleUtil.activityOnPause()
                                }
                                ON_RESUME -> {
                                    lifecycleUtil.activityOnResume()
                                }
                                ON_CREATE -> {
                                    //nothing
                                }
                                ON_START -> {
                                    lifecycleUtil.activityOnStart()
                                }
                                ON_STOP -> {
                                    lifecycleUtil.activityOnStop()
                                }
                                ON_DESTROY -> {
                                    lifecycleUtil.onActivityDestroyed(this)
                                }
                                ON_ANY -> {
                                    //nothing
                                }
                            }
                        },
                    )
                }
            }
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        baseVideoView.eventEmitter.on(
            "activitySaveInstanceState"
        ) { super.onSaveInstanceState(bundle) }
        lifecycleUtil.activityOnSaveInstanceState(bundle)
    }
}
