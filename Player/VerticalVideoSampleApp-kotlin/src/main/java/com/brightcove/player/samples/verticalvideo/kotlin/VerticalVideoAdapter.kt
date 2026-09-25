package com.brightcove.player.samples.verticalvideo.kotlin

import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.brightcove.player.event.AbstractEvent
import com.brightcove.player.event.Event
import com.brightcove.player.event.EventType
import com.brightcove.player.mediacontroller.BrightcoveMediaController
import com.brightcove.player.model.Video
import com.brightcove.player.samples.verticalvideo.kotlin.databinding.ItemVerticalVideoBinding
import com.brightcove.player.view.BrightcoveExoPlayerVideoView
import coil3.load
import coil3.request.crossfade

/**
 * A vertical [ListAdapter] where each page hosts its own Brightcove player.
 *
 * Playback ownership is explicit and lives in this adapter, not in the holder:
 *
 *  - [activate] is the single entry point that decides which page may play. It is
 *    driven by [androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback] and is
 *    idempotent, so a late bind / selection race cannot start two players.
 *  - Only the active page ever queues its [Video] into the SDK; adjacent pages just
 *    show their poster, so a swipe does not spin up decoders or spend bandwidth on the
 *    pages either side of the one being watched.
 *  - A page that leaves the active slot has its player cleared and its listeners
 *    unregistered, and every recycled holder destroys its player, so offscreen pages
 *    do not leak decoders.
 *  - A holder's playback listeners exist only while that holder is the active page, and
 *    each listener additionally ignores an [Event] that names a different [Video] than
 *    the one the holder is bound to, so a callback that resolves after the holder was
 *    rebound is ignored.
 *
 * The aspect-fill / crop behavior iOS gets from `.resizeAspectFill` is applied here
 * through `renderView.zoomIn()` — the same call the React Native bridge uses for
 * `videoScalingMode="fill"`.
 */
class VerticalVideoAdapter : ListAdapter<Video, VerticalVideoAdapter.PageHolder>(DIFF_CALLBACK) {

    /** Currently attached holders by adapter position. ViewPager2 owns the RecyclerView. */
    private val attachedHolders = SparseArray<PageHolder>()

    /** The adapter position currently allowed to play, or [RecyclerView.NO_POSITION]. */
    private var activePosition = RecyclerView.NO_POSITION

    /** The holder currently allowed to play. Kept directly so teardown can always release it. */
    private var activeHolder: PageHolder? = null

    /** Whether the feed is resumed; a paused feed must not autoplay when a page attaches. */
    private var isResumed = true

    /** Whether the active page was playing when the host went to background. */
    private var wasPlayingBeforeBackground = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
        val binding = ItemVerticalVideoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PageHolder(binding)
    }

    override fun onBindViewHolder(holder: PageHolder, position: Int) {
        attachedHolders.put(position, holder)
        holder.bind(getItem(position))
        reconcile(holder, position)
    }

    override fun onViewAttachedToWindow(holder: PageHolder) {
        super.onViewAttachedToWindow(holder)
        val position = holder.bindingAdapterPosition
        if (position != RecyclerView.NO_POSITION) {
            attachedHolders.put(position, holder)
        }
        reconcile(holder, position)
    }

    override fun onViewDetachedFromWindow(holder: PageHolder) {
        super.onViewDetachedFromWindow(holder)
        val position = holder.bindingAdapterPosition
        removeHolder(holder, position)
        // An attached-but-offscreen page must not keep playing or hold the decoder.
        if (holder !== activeHolder) {
            holder.deactivate()
        }
    }

    override fun onViewRecycled(holder: PageHolder) {
        super.onViewRecycled(holder)
        val position = holder.bindingAdapterPosition
        removeHolder(holder, position)
        if (holder === activeHolder) {
            activeHolder = null
        }
        // A recycled holder can never be the visible page; always tear the player down.
        holder.release()
    }

    /**
     * Drops [holder] from the attached-holder map. When the adapter position is already
     * [RecyclerView.NO_POSITION] (the common case by recycle time) the map is scanned so
     * a stale holder cannot pin its decoder through the map.
     */
    private fun removeHolder(holder: PageHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION && attachedHolders.get(position) === holder) {
            attachedHolders.remove(position)
            return
        }
        for (i in attachedHolders.size() - 1 downTo 0) {
            if (attachedHolders.valueAt(i) === holder) {
                attachedHolders.removeAt(i)
            }
        }
    }

    /**
     * Declares [position] as the only page allowed to play. Safe to call with an
     * out-of-range position (e.g. before the list arrives) or repeatedly; the previous
     * active page is always deactivated first.
     */
    fun activate(position: Int) {
        if (position < 0 || position >= itemCount) {
            deactivateAll()
            return
        }

        if (position != activePosition) {
            activeHolder?.deactivate()
            activeHolder = null
        }
        activePosition = position

        val holder = attachedHolders.get(position) ?: return
        activeHolder = holder
        if (isResumed) {
            holder.play()
        } else {
            holder.deactivate()
        }
    }

    fun pauseActive() {
        isResumed = false
        // Backgrounding is not a page change: preserve the queue and playhead so a
        // playing page can resume where it stopped. Also remember a user-paused page
        // so returning to the app does not start it behind their back.
        wasPlayingBeforeBackground = activeHolder?.pauseForBackground() == true
    }

    fun resumeActive() {
        isResumed = true
        if (wasPlayingBeforeBackground) {
            activeHolder?.resumeFromBackground()
        }
        wasPlayingBeforeBackground = false
    }

    fun deactivateAll() {
        activeHolder?.release()
        activeHolder = null
        activePosition = RecyclerView.NO_POSITION
    }

    private fun reconcile(holder: PageHolder, position: Int) {
        if (position == activePosition && isResumed) {
            activeHolder = holder
            holder.play()
        } else if (holder !== activeHolder) {
            holder.deactivate()
        }
    }

    inner class PageHolder(
        val binding: ItemVerticalVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val videoView: BrightcoveExoPlayerVideoView = binding.brightcoveVideoView

        /** The video this holder is currently bound to; guards against stale callbacks. */
        private var boundVideo: Video? = null

        /** Whether [boundVideo] has been queued into the SDK player. */
        private var queued = false

        private var readyListener = -1
        private var playListener = -1
        private var pauseListener = -1
        private var completeListener = -1
        private var errorListener = -1
        private var listenersRegistered = false

        init {
            // The video view is inflated from XML, so its onFinishInflate already ran
            // finishInitialization(); calling it again would double-register controllers.
            //
            // Suppress the SDK's default media controller (play/pause, seek bar, captions,
            // fullscreen). A TikTok-style feed uses tap-to-toggle plus the overlay defined
            // here, not the stock control bar; the fullscreen button in particular has no
            // meaning inside a paged full-screen layout.
            videoView.setMediaController(null as BrightcoveMediaController?)

            binding.root.setOnClickListener { togglePlayPause() }
        }

        fun bind(video: Video) {
            if (boundVideo?.id == video.id) return

            boundVideo = video
            queued = false
            videoView.clear()

            binding.videoDescription.text = video.name ?: video.description
            cancelPosterAnimation()
            updatePlayIndicator(playing = false)
            binding.poster.alpha = 1f

            (video.posterImage ?: video.stillImageUri)?.let { still ->
                binding.poster.load(still.toString()) {
                    crossfade(true)
                }
            }
            applyAspectFill()
        }

        /** Queue (once), register this page's listeners, and start playback. */
        fun play() {
            val video = boundVideo ?: return
            if (!queued) {
                registerPlaybackListeners()
                // Clear first so the queue always holds exactly this page's video. Without
                // it, replay-after-completion would append a duplicate (ExoMediaPlayback.add
                // appends at the end of the playlist).
                videoView.clear()
                videoView.add(video)
                queued = true
            }
            applyAspectFill()
            videoView.start()
        }

        /**
         * Pause for host backgrounding without discarding the queue or playhead, so the
         * page can resume where it stopped. Returns whether it was playing.
         */
        fun pauseForBackground(): Boolean {
            val wasPlaying = videoView.isPlaying
            videoView.pause()
            updatePlayIndicator(playing = false)
            return wasPlaying
        }

        /** Resume a page that was playing before the host was backgrounded. */
        fun resumeFromBackground() {
            if (!queued) return
            applyAspectFill()
            videoView.start()
        }

        /** Stop playback and free the decoder while keeping the poster for a later re-play. */
        fun deactivate() {
            videoView.pause()
            videoView.clear()
            unregisterPlaybackListeners()
            queued = false
            cancelPosterAnimation()
            binding.poster.alpha = 1f
            updatePlayIndicator(playing = false)
        }

        /** Tear the native player down entirely; called only when the holder is recycled. */
        fun release() {
            deactivate()
            videoView.playback?.destroyPlayer()
            boundVideo = null
        }

        private fun togglePlayPause() {
            if (videoView.isPlaying) {
                videoView.pause()
                updatePlayIndicator(playing = false)
            } else {
                play()
            }
        }

        /** Show the play glyph when paused and hide it while playing. */
        private fun updatePlayIndicator(playing: Boolean) {
            binding.playIcon.setImageResource(R.drawable.ic_play_arrow_white_24dp)
            binding.playIcon.alpha = if (playing) 0f else 1f
        }

        /**
         * Uses the render surface's zoom to crop the video to the viewport, matching
         * iOS `.resizeAspectFill`. This is the surface fit/crop switch, not the
         * pinch-gesture ZoomController.
         */
        private fun applyAspectFill() {
            videoView.renderView?.zoomIn()
        }

        private fun registerPlaybackListeners() {
            if (listenersRegistered) return
            val emitter = videoView.eventEmitter
            readyListener = emitter.on(EventType.READY_TO_PLAY) { if (isForBoundVideo(it)) hidePoster() }
            playListener = emitter.on(EventType.DID_PLAY) { if (isForBoundVideo(it)) onDidPlay() }
            pauseListener = emitter.on(EventType.DID_PAUSE) { if (isForBoundVideo(it)) onDidPause() }
            completeListener = emitter.on(EventType.COMPLETED) { if (isForBoundVideo(it)) onCompleted() }
            errorListener = emitter.on(EventType.ERROR) { if (isForBoundVideo(it)) onError(it) }
            listenersRegistered = true
        }

        private fun unregisterPlaybackListeners() {
            if (!listenersRegistered) return
            val emitter = videoView.eventEmitter
            listOf(
                EventType.READY_TO_PLAY to readyListener,
                EventType.DID_PLAY to playListener,
                EventType.DID_PAUSE to pauseListener,
                EventType.COMPLETED to completeListener,
                EventType.ERROR to errorListener
            ).forEach { (type, token) ->
                if (token != -1) emitter.off(type, token)
            }
            listenersRegistered = false
        }

        /**
         * True when [event] belongs to the video this holder is currently bound to. The
         * SDK dispatches events asynchronously, so a page can be rebound (or released)
         * before a previously emitted event is handled; those must not paint this page.
         */
        private fun isForBoundVideo(event: Event): Boolean {
            val bound = boundVideo ?: return false
            val eventVideo = event.properties[AbstractEvent.VIDEO]
            if (eventVideo is Video) {
                return eventVideo === bound || eventVideo.id == bound.id
            }
            // Source-selection errors can arrive before currentVideo is assigned. Accept an
            // unscoped event only for the holder that currently owns playback and has queued
            // this video; deactivated holders have no listeners, so a stale event cannot pass.
            val current = videoView.playback?.currentVideo
            return if (current != null) {
                current.id == bound.id
            } else {
                this === activeHolder && queued
            }
        }

        private fun cancelPosterAnimation() {
            binding.poster.animate().cancel()
        }

        private fun hidePoster() {
            binding.poster.animate().alpha(0f).setDuration(250).start()
        }

        private fun onDidPlay() {
            binding.playIcon.alpha = 0f
            binding.poster.alpha = 0f
        }

        private fun onDidPause() {
            if (videoView.isPlaying) return
            binding.playIcon.alpha = 1f
        }

        private fun onCompleted() {
            // The queue is spent; re-queuing on the next tap restarts from the beginning.
            queued = false
            cancelPosterAnimation()
            binding.poster.alpha = 1f
            binding.playIcon.setImageResource(R.drawable.ic_play_arrow_white_24dp)
            binding.playIcon.alpha = 1f
        }

        private fun onError(event: Event) {
            Log.e(TAG, "Playback error on '${boundVideo?.name}': ${event.properties}")
            // Reset the queue so a tap re-adds the source and retries, instead of
            // start()ing against the failed source. Keep the poster visible.
            videoView.clear()
            queued = false
            cancelPosterAnimation()
            binding.poster.alpha = 1f
            updatePlayIndicator(playing = false)
        }
    }

    companion object {
        private const val TAG = "VerticalVideo"

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Video>() {
            override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean =
                oldItem.id == newItem.id &&
                    oldItem.name == newItem.name &&
                    oldItem.description == newItem.description &&
                    oldItem.posterImage == newItem.posterImage
        }
    }
}
