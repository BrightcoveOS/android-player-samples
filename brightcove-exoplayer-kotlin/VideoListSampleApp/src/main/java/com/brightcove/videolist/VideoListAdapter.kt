package com.brightcove.videolist

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.brightcove.player.event.EventType
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcoveExoPlayerVideoView
import com.brightcove.videolist.databinding.ItemViewBinding

class VideoListAdapter : ListAdapter<Video, VideoListAdapter.ViewHolder>(DIFF_CALLBACK) {

    // This is used to keep track of the playback position for each video; remove it and its associated logic to play from the start always
    private val playheadPositions = SparseArray<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Get Video information
        val video = getItem(position)
        holder.video = video
        holder.binding.videoTitleText.text = video.getStringProperty(Video.Fields.NAME)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.video?.let { video ->
            holder.videoView.add(video)
            holder.videoView.seekTo(playheadPositions.get(holder.absoluteAdapterPosition, 0L))
            holder.videoView.start()
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        playheadPositions.put(holder.absoluteAdapterPosition, holder.videoView.currentPositionLong)
        holder.videoView.stopPlayback()
        holder.videoView.clear()
        holder.videoView.playback.destroyPlayer()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        val childCount = recyclerView.childCount
        //We need to stop the player to avoid a potential memory leak.
        for (i in 0 until childCount) {
            val holder = recyclerView.findViewHolderForAdapterPosition(i) as? ViewHolder?
            holder?.videoView?.stopPlayback()
        }
    }

    class ViewHolder (val binding: ItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val videoView = BrightcoveExoPlayerVideoView(itemView.context)
        var video: Video? = null

        init {
            binding.videoFrame.addView(videoView)
            videoView.finishInitialization()

            val eventEmitter = videoView.eventEmitter
            eventEmitter.on(EventType.ENTER_FULL_SCREEN) {
                //You can set listeners on each Video View
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Video>() {
            override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean =
                oldItem.equals(newItem)
        }
    }
}