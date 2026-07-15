package com.brightcove.player.samples.basiccastbrightcovereceiver.kotlin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brightcove.cast.DefaultSessionManagerListener
import com.brightcove.cast.GoogleCastComponent
import com.brightcove.cast.model.SplashScreen
import com.brightcove.cast.util.BrightcoveChannelUtil
import com.brightcove.player.samples.basiccastbrightcovereceiver.kotlin.databinding.ActivityBasicCastBrightcoveReceiverBinding
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.PlaylistListener
import com.brightcove.player.event.EventEmitter
import com.brightcove.player.event.EventEmitterImpl
import com.brightcove.player.model.Playlist
import com.brightcove.player.model.Video
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.Session

/**
 * Launcher screen for the Cast sample: loads a Video Cloud playlist, lists it in a
 * RecyclerView, and hands the selected video to [VideoPlayerActivity] for local or
 * Cast playback, optionally with server-side ad insertion.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBasicCastBrightcoveReceiverBinding
    private lateinit var adapter: VideoListAdapter
    private var adConfigId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBasicCastBrightcoveReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val checkPlayVideoWithSsai: CheckBox = binding.chkBoxPlayVideoWithSsai
        val videoListView: RecyclerView = binding.videoListView

        videoListView.layoutManager =  LinearLayoutManager(this)

        adapter = VideoListAdapter{view, video ->
            itemClicked(view, video)
        }
        videoListView.adapter = adapter

        val eventEmitter: EventEmitter = EventEmitterImpl()

        val catalog = Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
            .setPolicy(getString(R.string.sdk_demo_policy))
            .build()

        catalog.findPlaylistByReferenceID(
            getString(R.string.playlistReferenceId),
            object : PlaylistListener() {
                override fun onPlaylist(playlist: Playlist) {
                    adapter.submitList(playlist.videos)
                }
            })

        val castContext = CastContext.getSharedInstance()
        castContext?.sessionManager?.addSessionManagerListener(defaultSessionManagerListener)


        checkPlayVideoWithSsai.setOnCheckedChangeListener{_, isChecked ->
            adConfigId = if(isChecked) getString(R.string.sdk_demo_ad_config_id) else ""
        }
    }

    private fun itemClicked(view: View?, video: Video) {
        val intent = Intent(this, VideoPlayerActivity::class.java)

        intent.putExtra(Constants.INTENT_EXTRA_VIDEO_ID, video.id)
        intent.putExtra(Constants.INTENT_EXTRA_AD_CONFIG_ID, adConfigId)

        val imagePair: Pair<View, String> = Pair.create(view, getString(R.string.transition_image))

        //noinspection unchecked
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imagePair)
        ActivityCompat.startActivity(this, intent, options.toBundle())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menu?.let { GoogleCastComponent.setUpMediaRouteButton(this, it) }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        val castContext = CastContext.getSharedInstance()
        castContext?.sessionManager?.removeSessionManagerListener(defaultSessionManagerListener)
    }

    private val defaultSessionManagerListener: DefaultSessionManagerListener =
        object : DefaultSessionManagerListener() {
            override fun onSessionStarted(castSession: Session, s: String) {
                super.onSessionStarted(castSession, s)
                val src = getString(R.string.sdk_demo_splash_screen_url)
                BrightcoveChannelUtil.castSplashScreen((castSession as CastSession),
                    SplashScreen(src)
                )
            }
        }
}