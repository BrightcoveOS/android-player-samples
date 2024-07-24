package com.brightcove.videolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.brightcove.player.event.EventType
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcoveExoPlayerVideoView
import com.brightcove.player.view.BrightcoveVideoView
import com.brightcove.videolist.databinding.ItemViewBinding

class VideoListAdapter : ListAdapter<Video, VideoListAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Get Video information
        holder.bind(getItem(position))
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.videoView.start()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.videoView.stopPlayback()
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

    class ViewHolder (private val binding: ItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        lateinit var videoView: BrightcoveVideoView

        fun bind(video: Video){

            binding.apply {

                videoView = BrightcoveExoPlayerVideoView(itemView.context)
                videoFrame.addView(videoView)
                videoView.finishInitialization()

                val eventEmitter = videoView.eventEmitter
                eventEmitter.on(EventType.ENTER_FULL_SCREEN) {
                    //You can set listeners on each Video View
                }

                videoTitleText.text = video.getStringProperty(Video.Fields.NAME)
                videoView.clear()
                videoView.add(video)
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