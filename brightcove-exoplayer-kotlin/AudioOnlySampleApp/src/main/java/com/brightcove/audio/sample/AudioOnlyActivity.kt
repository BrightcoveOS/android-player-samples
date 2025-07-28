package com.brightcove.audio.sample

import android.app.Activity
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import androidx.annotation.OptIn
import androidx.core.view.MenuItemCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brightcove.audio.sample.databinding.ItemAudioBinding
import com.brightcove.playback.notification.BackgroundPlaybackNotification
import com.brightcove.player.appcompat.BrightcovePlayerActivity
import com.brightcove.player.display.ExoPlayerVideoDisplayComponent
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.PlaylistListener
import com.brightcove.player.model.Playlist
import com.brightcove.player.playback.MediaNotificationActions
import com.brightcove.player.playback.MediaPlayback
import com.brightcove.player.playback.PlaybackNotification
import com.brightcove.player.playback.PlaybackNotificationConfig
import com.brightcove.player.view.BrightcoveExoPlayerVideoView
import com.squareup.picasso.Picasso
import com.brightcove.audio.sample.databinding.ActivityAudioOnlyBinding as ViewBinding
import com.brightcove.player.model.Video as Media


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
        val videoDisplayComponent = baseVideoView.videoDisplay as ExoPlayerVideoDisplayComponent?
        videoDisplayComponent?.let {
            if (it.playbackNotification == null) {
                it.playbackNotification = createPlaybackNotification()
            }
        }
        catalog.findPlaylistByReferenceID(getString(R.string.reference_id), playlistListener)
    }

    private fun createPlaybackNotification() : PlaybackNotification? {
        val videoDisplayComponent = baseVideoView.videoDisplay as ExoPlayerVideoDisplayComponent?
        val notification = BackgroundPlaybackNotification.getInstance(this)
        val config = PlaybackNotificationConfig(this)
        // customizeNotification(config, this) // uncomment to play with the media notification customization
        notification?.setConfig(config)
        notification?.playback = videoDisplayComponent?.playback
        return notification
    }

    @OptIn(UnstableApi::class)
    private fun customizeNotification(
        config: PlaybackNotificationConfig,
        context: Context
    ) {
        config
            .setStreamTypes(*PlaybackNotification.StreamType.entries.toTypedArray())
            .setUseRewindAction(false)
            .setUseRewindActionInCompactView(false)
            .setUseNextAction(false)
            .setUseNextActionInCompactView(false)
            .setUsePreviousAction(false)
            .setUsePreviousActionInCompactView(false)
            .setUseFastForwardAction(false)
            .setUseFastForwardActionInCompactView(false)
            // customization of the text displayed on the media notification, as well as the behavior when clicking the notification
            .setAdapter(object : PlaybackNotification.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(playback: MediaPlayback<*>?): CharSequence {
                    return "Title blah" // Replace with actual title logic
                }

                override fun createCurrentContentIntent(playback: MediaPlayback<*>?): PendingIntent? {
                    // Create an intent to launch your activity when the notification is clicked
                    val intent = Intent(context, AudioOnlyActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) // flag to pick up the existing activity, instead of always create a new one
                    // You might want to add extras to the intent to pass data to your activity
                    // intent.putExtra("key", "value")
                    // Create a PendingIntent from the intent
                    return PendingIntent.getActivity(
                        context,
                        0, // Request code (can be any unique integer)
                        intent,
                        PendingIntent.FLAG_IMMUTABLE // Flags to control how the PendingIntent is handled
                    )
                }

                override fun getCurrentContentText(playback: MediaPlayback<*>?): CharSequence {
                    return "Text" // Replace with actual text logic
                }

                override fun getCurrentSubText(playback: MediaPlayback<*>?): CharSequence {
                    return "Subtext" // Replace with actual subtext logic
                }

            })
            // customization of the actions/buttons displayed on the media notification - will be applied on Android 13+ only
            // Note: when we set a MediaNotificationActions, the setUse[something]Action setters are ignored; we need to customize it here instead
            .setMediaNotificationActions(
                MediaNotificationActions(
                customActionsProvider = { mediaSession ->
                    val fastForwardCommandButton =
                        CommandButton.Builder(CommandButton.ICON_FAST_FORWARD)
                            .setSessionCommand(SessionCommand("CUSTOM_ACTION_FF", Bundle.EMPTY))
                            .setEnabled(true)
                            .setDisplayName("ff")
                            .setSlots(CommandButton.SLOT_FORWARD)
                            .build()

                    val likeCommandButton = CommandButton.Builder(CommandButton.ICON_HEART_FILLED)
                        .setSessionCommand(SessionCommand("CUSTOM_ACTION_LIKE", Bundle.EMPTY))
                        .setEnabled(true)
                        .setDisplayName("like")
                        .setSlots(CommandButton.SLOT_BACK)
                        .build()

                    val sessionCommandsBuilder =
                        MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                    fastForwardCommandButton.sessionCommand?.let { sessionCommandsBuilder.add(it) }
                    likeCommandButton.sessionCommand?.let { sessionCommandsBuilder.add(it) }
                    val sessionCommands = sessionCommandsBuilder.build()
                    val playerCommandsBuilder =
                        MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS.buildUpon()
                    playerCommandsBuilder.remove(Player.COMMAND_SEEK_TO_PREVIOUS)
                    playerCommandsBuilder.remove(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                    playerCommandsBuilder.remove(Player.COMMAND_SEEK_TO_NEXT)
                    playerCommandsBuilder.remove(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)

                    val playerCommands = playerCommandsBuilder.build()

                    // Custom button preferences and commands to configure the platform session.
                    MediaSession.ConnectionResult.AcceptedResultBuilder(mediaSession)
                        .setAvailablePlayerCommands(playerCommands)
                        .setMediaButtonPreferences(
                            mutableListOf(
                                fastForwardCommandButton,
                                likeCommandButton
                            )
                        )
                        .setAvailableSessionCommands(sessionCommands)
                },
                customCommandHandler = { mediaSession, controllerInfo, customCommand, args ->
                    when (customCommand?.customAction) {
                        "CUSTOM_ACTION_FF" -> {
                            player.seekTo(player.currentPositionLong + 15000)
                            true
                        }

                        "CUSTOM_ACTION_LIKE" -> {
                            // some action
                            true
                        }

                        else -> false
                    }
                }
            ))
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
