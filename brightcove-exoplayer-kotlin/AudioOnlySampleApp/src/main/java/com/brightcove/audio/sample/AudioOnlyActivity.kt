package com.brightcove.audio.sample

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brightcove.audio.sample.databinding.ItemAudioBinding
import com.brightcove.player.appcompat.BrightcovePlayerActivity
import com.brightcove.player.display.ExoPlayerVideoDisplayComponent
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.PlaylistListener
import com.brightcove.player.model.Playlist
import com.brightcove.player.playback.PlaybackNotification
import com.brightcove.player.playback.PlaybackNotificationConfig
import com.brightcove.player.view.BrightcoveExoPlayerVideoView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.squareup.picasso.Picasso
import com.brightcove.audio.sample.databinding.ActivityAudioOnlyBinding as ViewBinding
import com.brightcove.player.model.Video as Media
import com.brightcove.playback.notification.BackgroundPlaybackNotification


class AudioOnlyActivity : BrightcovePlayerActivity() {

    private lateinit var binding: ViewBinding

    private val catalog: Catalog by lazy {
        Catalog.Builder(player.eventEmitter, getString(R.string.account_id))
            .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
            .setPolicy(getString(R.string.policy_id))
            .build()
    }

    private val playlistListener = object : PlaylistListener() {
        override fun onPlaylist(playlist: Playlist) {
            setPlaylist(playlist)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        baseVideoView = binding.brightcoveVideoView
        setSupportActionBar(binding.toolbar)
        setUpPlayer()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_github -> openBrowser(GITHUB_URL)
        R.id.action_shuffle -> onClickShuffle(item)
        R.id.action_repeat -> onClickRepeat(item)
        else -> super.onOptionsItemSelected(item)
    }

    private fun onClickRepeat(item: MenuItem): Boolean {
        exoPlayer.repeatMode = when (exoPlayer.repeatMode) {
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
            else -> throw IllegalStateException()
        }.also { newRepeatMode ->
            when (newRepeatMode) {
                Player.REPEAT_MODE_ALL -> {
                    item.setIcon(R.drawable.ic_repeat_white_24dp)
                    MenuItemCompat.setIconTintList(item, ColorStateList.valueOf(Color.GREEN))
                }
                Player.REPEAT_MODE_OFF -> {
                    item.setIcon(R.drawable.ic_repeat_off_white_24dp)
                    MenuItemCompat.setIconTintList(item, null)
                }
                Player.REPEAT_MODE_ONE -> {
                    item.setIcon(R.drawable.ic_repeat_once_white_24dp)
                    MenuItemCompat.setIconTintList(item, ColorStateList.valueOf(Color.GREEN))
                }
            }
        }
        return true
    }

    private fun onClickShuffle(item: MenuItem): Boolean {
        exoPlayer.shuffleModeEnabled = !exoPlayer.shuffleModeEnabled
        if (exoPlayer.shuffleModeEnabled) {
            MenuItemCompat.setIconTintList(item, ColorStateList.valueOf(Color.GREEN))
        } else {
            MenuItemCompat.setIconTintList(item, null)
        }
        return true
    }

    private fun onClick(media: Media) {
        player.currentIndex = player.playback.playlist.indexOf(media)
    }

    private fun setUpPlayer() {
        player.playback.notification?.let {
            player.playback.notification.setConfig(
                PlaybackNotificationConfig(this)
                    .setStreamTypes(*PlaybackNotification.StreamType.values())
            )
        }
        val videoDisplayComponent = baseVideoView.videoDisplay as ExoPlayerVideoDisplayComponent?
        videoDisplayComponent?.let {
            if (videoDisplayComponent.playbackNotification == null) {
                videoDisplayComponent.playbackNotification = createPlaybackNotification()
            }
        }
        catalog.findPlaylistByReferenceID(getString(R.string.reference_id), playlistListener)
    }

    private fun createPlaybackNotification() : PlaybackNotification? {
        val videoDisplayComponent = baseVideoView.videoDisplay as ExoPlayerVideoDisplayComponent?
        val notification = BackgroundPlaybackNotification.getInstance(this)
        notification?.setConfig(PlaybackNotificationConfig(this))
        notification?.playback = videoDisplayComponent?.playback
        return notification
    }

    private fun setPlaylist(playlist: Playlist) = binding.run {
        player.addAll(playlist.videos)
        list.layoutManager = LinearLayoutManager(this@AudioOnlyActivity)
        list.adapter = PlaylistAdapter(playlist.videos, ::onClick)
        fadeOut(loading); fadeIn(list, player)
    }

    private class PlaylistAdapter(
        private val playlist: List<Media>,
        private val onClick: (Media) -> Unit
    ) : RecyclerView.Adapter<ItemViewHolder>() {

        override fun getItemCount(): Int = playlist.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_audio, parent, false), onClick
        )

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder bind playlist[position]
        }
    }

    private class ItemViewHolder(
        itemView: View,
        private val onClick: (Media) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding: ItemAudioBinding by lazy {
            ItemAudioBinding.bind(itemView)
        }

        infix fun bind(media: Media) = binding.run {
            Picasso.get().load(media.thumbnail.toString())
                .placeholder(R.drawable.cover_art_default)
                .error(R.drawable.cover_art_default)
                .into(albumArt)
            title.text = media.name
            subtitle.text = media.description
            root.setOnClickListener {
                onClick(media)
            }
        }
    }

    companion object {

        private const val GITHUB_URL = "https://github.com/BrightcoveOS/android-player-samples"

        private val BrightcovePlayerActivity.player: BrightcoveExoPlayerVideoView
            get() = baseVideoView as BrightcoveExoPlayerVideoView

        private val BrightcovePlayerActivity.mediaDisplay: ExoPlayerVideoDisplayComponent
            get() = player.videoDisplay as ExoPlayerVideoDisplayComponent

        private val BrightcovePlayerActivity.exoPlayer: ExoPlayer
            get() = mediaDisplay.exoPlayer

        private fun Activity.openBrowser(url: String) = try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))); true
        } catch (e: ActivityNotFoundException) {
            false
        }

        private fun fadeIn(vararg views: View) {
            views.onEach { view ->
                if (view.visibility != View.VISIBLE) {
                    view.visibility = View.VISIBLE
                }
                view.animation = AlphaAnimation(0f, 1f).apply {
                    duration = 1000; interpolator = DecelerateInterpolator()
                }.also { it.start() }
            }
        }

        private fun fadeOut(vararg views: View) {
            views.onEach { view ->
                view.animation = AlphaAnimation(1f, 0f).apply {
                    duration = 1000;interpolator = AccelerateInterpolator()
                }
                view.postDelayed({
                    view.visibility = View.GONE
                }, 1000)
            }
        }
    }
}
