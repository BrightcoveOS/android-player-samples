package com.brightcove.player.samples.verticalvideo.kotlin

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.CatalogError
import com.brightcove.player.edge.PlaylistListener
import com.brightcove.player.event.EventEmitterImpl
import com.brightcove.player.model.Playlist
import com.brightcove.player.samples.verticalvideo.kotlin.databinding.ActivityVerticalVideoSampleBinding

/**
 * A full-bleed vertical (TikTok / Reels style) paging video feed built on [ViewPager2].
 *
 * Every page owns its own [com.brightcove.player.view.BrightcoveExoPlayerVideoView].
 * [VerticalVideoAdapter] is the single authority on which page is allowed to play:
 * exactly one page is active at a time, and a page that is scrolled away is paused
 * and (once recycled) released. This is the Android counterpart of the iOS
 * `VerticalPlayer` sample.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerticalVideoSampleBinding
    private lateinit var adapter: VerticalVideoAdapter
    private var restoredPosition = 0

    private val catalog: Catalog by lazy {
        Catalog.Builder(EventEmitterImpl(), getString(R.string.sdk_demo_account))
            .setPolicy(getString(R.string.sdk_demo_policy))
            .build()
    }

    private val playlistListener = object : PlaylistListener() {
        override fun onPlaylist(playlist: Playlist) {
            onPlaylistLoaded(playlist)
        }

        override fun onError(errors: List<CatalogError>) {
            Log.e(TAG, "Failed to load playlist: $errors")
            binding.loading.visibility = View.GONE
        }
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            adapter.activate(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerticalVideoSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = VerticalVideoAdapter()
        binding.verticalPager.adapter = adapter
        // Keep one page on each side attached so the neighboring poster is already visible
        // when a swipe starts. Adjacent pages never start playback — the adapter only
        // activates the selected page — so this pre-attaches UI, not decoders.
        binding.verticalPager.offscreenPageLimit = 1
        binding.verticalPager.registerOnPageChangeCallback(pageChangeCallback)

        // ViewPager2 cannot restore its own position here: the list is empty until the
        // Catalog request returns, so there is nothing to scroll to at restore time.
        restoredPosition = savedInstanceState?.getInt(KEY_ACTIVE_POSITION) ?: 0

        catalog.findPlaylistByID(getString(R.string.sdk_demo_playlist_id), playlistListener)
    }

    private fun onPlaylistLoaded(playlist: Playlist) {
        val videos = playlist.videos
        if (videos.isNullOrEmpty()) {
            Log.w(TAG, "Playlist is empty; nothing to play.")
            binding.loading.visibility = View.GONE
            return
        }

        val target = restoredPosition.coerceIn(0, videos.size - 1)
        // AsyncListDiffer installs the list asynchronously; activate only after it is
        // committed, otherwise itemCount is still 0 and activate() would bail out.
        adapter.submitList(videos) {
            binding.verticalPager.setCurrentItem(target, false)
            adapter.activate(target)
        }
        binding.loading.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        adapter.resumeActive()
    }

    override fun onPause() {
        // Never keep playing audio while the feed is not visible; the bridge does the
        // equivalent when a React Native screen loses focus.
        adapter.pauseActive()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_ACTIVE_POSITION, binding.verticalPager.currentItem)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        binding.verticalPager.unregisterOnPageChangeCallback(pageChangeCallback)
        binding.verticalPager.adapter = null
        adapter.deactivateAll()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "VerticalVideo"
        private const val KEY_ACTIVE_POSITION = "vertical_video_active_position"
    }
}
