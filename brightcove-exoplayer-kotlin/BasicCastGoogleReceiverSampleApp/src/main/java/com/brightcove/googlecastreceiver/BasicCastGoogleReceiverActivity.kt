package com.brightcove.googlecastreceiver

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brightcove.cast.GoogleCastComponent
import com.brightcove.googlecastreceiver.databinding.ActivityBasicCastGoogleReceiverBinding
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.PlaylistListener
import com.brightcove.player.event.EventEmitter
import com.brightcove.player.event.EventEmitterImpl
import com.brightcove.player.model.Playlist
import com.brightcove.player.model.Video


class BasicCastGoogleReceiverActivity  : AppCompatActivity() {

    private lateinit var binding: ActivityBasicCastGoogleReceiverBinding
    private lateinit var adapter: VideoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBasicCastGoogleReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videoListView: RecyclerView = binding.videoListView

        videoListView.layoutManager = LinearLayoutManager(this)

        //manage the onClick Event
        adapter = VideoListAdapter { view, video ->
            itemClicked(view, video)
        }
        videoListView.adapter = adapter

        val eventEmitter: EventEmitter = EventEmitterImpl()

        val catalog = Catalog.Builder(eventEmitter, getString(R.string.account))
            .setPolicy(getString(R.string.policy))
            .build()

        catalog.findPlaylistByReferenceID(
            getString(R.string.playlistRefId),
            object : PlaylistListener() {
                override fun onPlaylist(playlist: Playlist) {
                    adapter.submitList(playlist.videos)
                }
            })

    }

    private fun itemClicked(view: View?, video: Video) {
        val intent = Intent(this, VideoPlayerActivity::class.java)

        intent.putExtra(VideoPlayerActivity.INTENT_EXTRA_VIDEO_ID, video.id)

        val imagePair: Pair<View, String> = Pair.create(view, getString(R.string.transition_image))

        //noinspection unchecked
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imagePair)
        ActivityCompat.startActivity(this, intent, options.toBundle())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        GoogleCastComponent.setUpMediaRouteButton(this, menu!!)
        return true
    }
}