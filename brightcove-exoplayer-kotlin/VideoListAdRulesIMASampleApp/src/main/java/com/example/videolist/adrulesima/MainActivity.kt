package com.example.videolist.adrulesima

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.PlaylistListener
import com.brightcove.player.event.EventEmitter
import com.brightcove.player.event.EventEmitterImpl
import com.brightcove.player.model.Playlist

class MainActivity : AppCompatActivity() {
    private var catalog: Catalog? = null
    private var videoListView: RecyclerView? = null
    private var adapterView: AdapterView? = null
    private var eventEmitter: EventEmitter = EventEmitterImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        videoListView = findViewById<View>(R.id.video_list_view) as RecyclerView

        adapterView = AdapterView()
        videoListView?.setAdapter(adapterView)

        catalog = Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
            .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
            .setPolicy(getString(R.string.sdk_demo_policy_key))
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