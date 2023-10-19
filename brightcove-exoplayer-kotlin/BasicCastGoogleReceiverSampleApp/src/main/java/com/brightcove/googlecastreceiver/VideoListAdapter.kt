package com.brightcove.googlecastreceiver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.brightcove.googlecastreceiver.databinding.ItemViewBinding
import com.brightcove.player.model.Video
import com.squareup.picasso.Picasso
import java.util.*
import java.util.concurrent.TimeUnit


typealias OnVideoClick = (View, Video) -> Unit

class VideoListAdapter(private val onVideoClick: OnVideoClick) : ListAdapter<Video, VideoListAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onVideoClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Get Video information
        holder.bind(getItem(position))
    }

    class ViewHolder (
        private val binding: ItemViewBinding,
        private val onVideoClick: OnVideoClick
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(video: Video){

            binding.apply {

                //Set Video info
                videoTitleText.text = video.name

                val descriptionObj: String? = video.properties[VideoPlayerActivity.PROPS_SHORT_DESCRIPTION]?.toString()

                if(!descriptionObj.isNullOrBlank())
                    videoDurationText.text = descriptionObj

                if (video.durationLong > 0) {
                    videoDurationText.text = millisecondsToString(video.durationLong)
                    videoDurationText.visibility = View.VISIBLE
                } else {
                    videoDurationText.text = ""
                    videoDurationText.visibility = View.GONE
                }

                val imageUri = video.stillImageUri
                if (imageUri == null) {
                    videoThumbnailImage.setImageResource(R.drawable.movie)
                } else {
                    Picasso.get()
                        .load(imageUri.toASCIIString())
                        .into(videoThumbnailImage)
                }

                //Adding onCLick Listener to the view
                videoThumbnailImage.setOnClickListener{
                    onVideoClick(this.root,video)
                }
            }
        }

        /**
         * Converts the given duration into a time span string.
         *
         * @param durationLong elapsed time as number of milliseconds.
         * @return the formatted time span string.
         */
        private fun millisecondsToString(durationLong: Long): String {
            var duration = durationLong
            val scale = TimeUnit.MILLISECONDS
            val builder = StringBuilder()
            val days = scale.toDays(duration)
            duration -= TimeUnit.DAYS.toMillis(days)
            if (days > 0) {
                builder.append(days)
                builder.append(if (days > 1) " days " else " day ")
            }
            val hours = scale.toHours(duration)
            duration -= TimeUnit.HOURS.toMillis(hours)
            if (hours > 0) {
                builder.append(String.format(Locale.getDefault(), "%02d:", hours))
            }
            val minutes = scale.toMinutes(duration)
            duration -= TimeUnit.MINUTES.toMillis(minutes)
            val seconds = scale.toSeconds(duration)
            builder.append(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds))
            return builder.toString()
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