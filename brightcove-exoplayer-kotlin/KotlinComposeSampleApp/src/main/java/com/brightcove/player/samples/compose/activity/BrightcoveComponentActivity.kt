package com.brightcove.player.samples.compose.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.brightcove.player.event.EventLogger
import com.brightcove.player.playback.PlaybackNotification
import com.brightcove.player.util.EventEmitterUtil
import com.brightcove.player.util.LifecycleUtil
import com.brightcove.player.view.BaseVideoView

open class BrightcoveComponentActivity constructor():
    ComponentActivity(), PlaybackNotification.OnRestorePlaybackHandler {

    val TAG: String = BrightcoveComponentActivity::class.java.simpleName

    var baseVideoView: BaseVideoView? = null
    var eventLogger: EventLogger? = null
    private var lifecycleUtil: LifecycleUtil? = null
    private var savedInstanceState: Bundle? = null


    fun showClosedCaptioningDialog() {
        baseVideoView?.closedCaptioningController?.showCaptionsDialog()
    }

    fun enterFullScreen() {
        baseVideoView?.eventEmitter?.emit("enterFullScreen")
    }

    fun exitFullScreen() {
        baseVideoView?.eventEmitter?.emit("exitFullScreen")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (baseVideoView == null || lifecycleUtil != null && lifecycleUtil?.baseVideoView === baseVideoView) {
            this.savedInstanceState = savedInstanceState
        } else {
            lifecycleUtil = LifecycleUtil(baseVideoView)
            lifecycleUtil?.onCreate(savedInstanceState, this)
            eventLogger = EventLogger(baseVideoView?.eventEmitter, true, this.javaClass.simpleName)
        }
    }

    fun initializeLifecycleUtil(baseVideoView : BaseVideoView) {
        if (this.baseVideoView == null) {
            this.baseVideoView = baseVideoView
            checkNotNull(baseVideoView) { "A BaseVideoView must be wired up to the layout." }
            lifecycleUtil = LifecycleUtil(baseVideoView)
            lifecycleUtil?.onCreate(savedInstanceState, this)
            eventLogger =
                EventLogger(this.baseVideoView?.eventEmitter, true, this.javaClass.simpleName)
        }
        savedInstanceState = null
    }

    override fun onStart() {
        Log.v(TAG, "onStart")
        super.onStart()
        lifecycleUtil?.activityOnStart()
    }

    override fun onPause() {
        Log.v(TAG, "onPause")
        super.onPause()
        lifecycleUtil?.activityOnPause()
    }

    override fun onResume() {
        Log.v(TAG, "onResume")
        super.onResume()
        lifecycleUtil?.activityOnResume()
    }

    override fun onRestart() {
        Log.v(TAG, "onRestart")
        super.onRestart()
        lifecycleUtil?.onRestart()
    }

    override fun onDestroy() {
        Log.v(TAG, "onDestroy")
        super.onDestroy()
        lifecycleUtil?.onActivityDestroyed(this)
    }

    override fun onStop() {
        Log.v(TAG, "onStop")
        super.onStop()
        lifecycleUtil?.activityOnStop()
    }

    protected override fun onSaveInstanceState(bundle: Bundle) {
        baseVideoView!!.eventEmitter.on(
            "activitySaveInstanceState"
        ) { super@BrightcoveComponentActivity.onSaveInstanceState(bundle) }
        lifecycleUtil?.activityOnSaveInstanceState(bundle)
    }
}