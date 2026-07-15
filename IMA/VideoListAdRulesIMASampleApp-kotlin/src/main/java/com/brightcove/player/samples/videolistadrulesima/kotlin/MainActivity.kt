package com.brightcove.player.samples.videolistadrulesima.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.PlaylistListener
import com.brightcove.player.event.EventEmitter
import com.brightcove.player.event.EventEmitterImpl
import com.brightcove.player.model.Playlist
import com.brightcove.player.samples.videolistadrulesima.kotlin.databinding.ActivityMainBinding

/**
 * Fetches a Brightcove playlist and displays each video in a scrolling
 * RecyclerView, with a dedicated player for every row.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var catalog: Catalog? = null
    private var adapterView: VideoListAdapter? = null
    private var eventEmitter: EventEmitter = EventEmitterImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapterView = VideoListAdapter()
        binding.videoListView.adapter = adapterView

        catalog = Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
            .setPolicy(getString(R.string.sdk_demo_policy))
            .build()

        catalog?.findPlaylistByReferenceID(
            getString(R.string.sdk_demo_playlist_reference),
            object : PlaylistListener() {
                override fun onPlaylist(playlist: Playlist) {
                    adapterView?.setVideoList(playlist.videos)
                }
            })
    }

    override fun onDestroy() {
        binding.videoListView.adapter = null
        binding.videoListView.removeAllViews()
        super.onDestroy()
    }
}