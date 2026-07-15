package com.brightcove.player.samples.videolistadrulesima.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.PlaylistListener
import com.brightcove.player.event.EventEmitter
import com.brightcove.player.event.EventEmitterImpl
import com.brightcove.player.model.Playlist

/**
 * Fetches a Brightcove playlist and displays each video in a scrolling
 * RecyclerView, with a dedicated player for every row.
 */
class MainActivity : AppCompatActivity() {
    private var catalog: Catalog? = null
    private var videoListView: RecyclerView? = null
    private var adapterView: VideoListAdapter? = null
    private var eventEmitter: EventEmitter = EventEmitterImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        videoListView = findViewById(R.id.video_list_view)

        adapterView = VideoListAdapter()
        videoListView?.setAdapter(adapterView)

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
        videoListView?.adapter = null
        videoListView?.removeAllViews()
        super.onDestroy()
    }
}