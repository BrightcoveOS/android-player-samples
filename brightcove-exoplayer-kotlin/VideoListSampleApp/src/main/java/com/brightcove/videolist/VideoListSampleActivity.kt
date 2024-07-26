package com.brightcove.videolist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.PlaylistListener
import com.brightcove.player.event.EventEmitter
import com.brightcove.player.event.EventEmitterImpl
import com.brightcove.player.model.Playlist
import com.brightcove.videolist.databinding.ActivityVideoListSampleBinding

class VideoListSampleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoListSampleBinding
    private lateinit var videoListView: RecyclerView
    private var eventEmitter: EventEmitter = EventEmitterImpl()
    private lateinit var adapter: VideoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoListSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoListView = binding.videoListView
        videoListView.layoutManager =  LinearLayoutManager(this)
        adapter = VideoListAdapter()

        videoListView.adapter = adapter

        val catalog = Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
            .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
            .setPolicy(getString(R.string.sdk_demo_policy_key))
            .build()

        catalog.findPlaylistByReferenceID(
            getString(R.string.sdk_demo_playlist_reference),
            object : PlaylistListener() {
                override fun onPlaylist(playlist: Playlist) {
                    adapter.submitList(playlist.videos)
                }
            })
    }

    override fun onDestroy() {
        videoListView.adapter = null
        videoListView.removeAllViews()
        super.onDestroy()
    }
}