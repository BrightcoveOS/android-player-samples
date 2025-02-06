package com.brightcove.offline.playback

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.brightcove.offline.playback.utils.ViewUtil
import com.brightcove.player.edge.OfflineCallback
import com.brightcove.player.edge.OfflineCatalog
import com.brightcove.player.model.Video
import com.brightcove.player.network.DownloadStatus
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Video list adapter can be used to show a list of videos on a [RecyclerView].
 */
class VideoListAdapter
/**
 * Constructors a new video list adapter.
 *
 * @param catalog  reference to the offline catalog.
 * @param listener reference to a listener
 * @throws IllegalArgumentException if the catalog or listener is null.
 */ internal constructor(
    /**
     * The catalog where the video in this list can be found.
     */
    private val catalog: OfflineCatalog,
    /**
     * The listener that can handle user interactions on a video item display.
     */
    private val listener: VideoListListener
) : RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {
    /**
     * The current list of videos.
     */
    private var videoList: MutableList<Video>? = null

    /**
     * A map of video identifiers and position of video in the list.
     */
    private val indexMap: MutableMap<String, Int> = HashMap()

    /**
     * A view holder that hold references to the UI components in a list item.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         * Reference the item view context
         */
        val context: Context = itemView.context

        /**
         * Reference to the thumbnail image view.
         */
        val videoThumbnailImage: ImageView = ViewUtil.findView(itemView, R.id.video_thumbnail_image)

        /**
         * Reference to the video title view.
         */
        val videoTitleText: TextView =
            ViewUtil.findView(itemView, R.id.video_title_text)

        /**
         * Reference to the video status information text view.
         */
        val videoStatusText: TextView =
            ViewUtil.findView(itemView, R.id.video_status_text)

        /**
         * Reference to the estimated size view.
         */
        val estimatedSizeText: TextView =
            ViewUtil.findView(itemView, R.id.estimated_size_text)

        /**
         * Reference to the video license information text view.
         */
        val videoLicenseText: TextView =
            ViewUtil.findView(itemView, R.id.video_license_text)

        /**
         * Reference to the video duration view.
         */
        val videoDurationText: TextView =
            ViewUtil.findView(itemView, R.id.video_duration_text)

        /**
         * Reference to the rent video button.
         */
        val rentButton: Button = ViewUtil.findView(itemView, R.id.rent_button)

        /**
         * Reference to the buy video button.
         */
        val buyButton: Button = ViewUtil.findView(itemView, R.id.buy_button)

        /**
         * Reference to the download video button.
         */
        val downloadButton: ImageButton =
            ViewUtil.findView(itemView, R.id.download_button)

        /**
         * Reference to the pause/resume download button.
         */
        val pauseButton: ImageButton =
            ViewUtil.findView(itemView, R.id.pause_button)

        /**
         * Reference to the pause/resume download button.
         */
        val resumeButton: ImageButton =
            ViewUtil.findView(itemView, R.id.reesume_button)

        /**
         * Reference to the delete video button.
         */
        val deleteButton: ImageButton =
            ViewUtil.findView(itemView, R.id.delete_button)

        /**
         * Reference to the download progress bar.
         */
        val downloadProgressBar: ContentLoadingProgressBar =
            ViewUtil.findView(itemView, R.id.download_progress_bar)

        /**
         * The currently linked video.
         */
        var video: Video? = null

        /**
         * Constructs a new view holder for the given item view.
         *
         * @param itemView reference to the item.
         */
        init {
            downloadProgressBar.progressDrawable.setColorFilter(
                Color.DKGRAY,
                PorterDuff.Mode.SRC_IN
            )
        }

        fun getVideo(callback: OfflineCallback<Video?>) {
            val result = video

            if (video?.isOfflinePlaybackAllowed == true) {
                catalog.findOfflineVideoById(video?.id, object : OfflineCallback<Video?> {
                    override fun onSuccess(offlineVideo: Video?) {
                        if (offlineVideo != null) {
                            callback.onSuccess(offlineVideo)
                        } else {
                            callback.onSuccess(video)
                        }
                    }

                    override fun onFailure(throwable: Throwable) {
                        callback.onSuccess(result)
                    }
                })
            } else {
                callback.onSuccess(result)
            }
        }
    }

    /**
     * Sets the list of videos to be shown. If this value is null, then the list will be empty.
     * Note that a local copy of the Playlist's video list is made
     *
     * @param videoList list of [Video] objects.
     */
    fun setVideoList(videoList: List<Video?>?) {
        this.videoList = ArrayList()
        if (videoList != null) {
            this.videoList?.addAll(videoList.filterNotNull())
        }
        buildIndexMap()
    }

    /**
     * Build the index map.
     */
    private fun buildIndexMap() {
        indexMap.clear()
        if (videoList != null) {
            var index = 0
            for (video in videoList!!) {
                indexMap[video.id] = index++
            }
        }
        notifyDataSetChanged()
    }

    /**
     * Causes item display for the specified video to be updated.
     *
     * @param video  the video that changed
     * @param status optional current download status.
     */
    /**
     * Causes item display for the specified video to be updated.
     *
     * @param video the video that changed
     */
    @JvmOverloads
    fun notifyVideoChanged(video: Video, status: DownloadStatus? = null) {
        val videoId = video.id
        if (indexMap.containsKey(videoId)) {
            indexMap[videoId]?.let {
                videoList?.set(it, video)
                notifyItemChanged(it, status)
            }
        }
    }

    /**
     * Removes a video from the list.
     *
     * @param video the video to be removed.
     */
    fun removeVideo(video: Video) {
        val videoId = video.id
        if (indexMap.containsKey(videoId)) {
            indexMap.remove(videoId)?.let {
                videoList?.removeAt(it)
            }
            buildIndexMap()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.video_item_view, parent, false)
        return ViewHolder(view)
    }

    /**
     * Updates the row at the given position to reflect the changes in the underlying data.
     *
     * @param holder   reference to the view holder.
     * @param position the position of the row that should be updated.
     * @param downloadStatus   optional current download status.
     */
    private fun updateView(
        holder: ViewHolder,
        position: Int,
        downloadStatus: DownloadStatus?
    ) {
        holder.video = videoList?.get(position)
        holder.getVideo(object : OfflineCallback<Video?> {
            override fun onSuccess(video: Video?) {
                handleViewState(holder, video, downloadStatus)
            }

            override fun onFailure(throwable: Throwable) {
                Log.e(TAG, "Error fetching video: ", throwable)
            }
        })
    }

    private fun handleViewState(holder: ViewHolder, video: Video?, status: DownloadStatus?) {
        if (video?.isOfflinePlaybackAllowed == true) {
            holder.estimatedSizeText.visibility = View.VISIBLE
            catalog.estimateSize(holder.video!!) { size: Long ->
                var sizeStr = ""
                val sizeMb = size / MEGABYTE_IN_BYTES.toDouble()
                if (sizeMb >= 1) {
                    sizeStr = sizeMb.toLong().toString() + " MB"
                } else if (sizeMb > 0 /* && sizeMb < 1*/) {
                    val sizeFormat = DecimalFormat("#.##")
                    sizeStr = sizeFormat.format(sizeMb) + " MB"
                } else {
                    sizeStr = "Unknown size"
                }
                holder.estimatedSizeText.text = sizeStr
            }

            if (video.isClearContent) {
                // Video is a Clear video -- show the download button only
                holder.videoLicenseText.setText(R.string.press_to_save)
                holder.videoStatusText.visibility = View.GONE
                holder.downloadButton.visibility = View.VISIBLE
                holder.rentButton.visibility = View.GONE
                holder.buyButton.visibility = View.GONE
                holder.deleteButton.visibility = View.GONE
                holder.downloadProgressBar.visibility = View.GONE
                holder.pauseButton.visibility = View.GONE
                holder.resumeButton.visibility = View.GONE

                if (video.isOfflineCopy) {
                    holder.videoStatusText.setText(R.string.video_download_clear_offline_copy)
                    holder.pauseButton.visibility = View.GONE
                    holder.resumeButton.visibility = View.GONE
                    holder.downloadButton.visibility = View.GONE
                    holder.deleteButton.visibility = View.VISIBLE
                }
            } else {
                // Video is a DRM video -- show the appropriate buy/rent buttons
                val expiryDate = video.licenseExpiryDate
                if (expiryDate == null) {
                    holder.videoLicenseText.setText(R.string.video_download_purchase_or_rent)
                    holder.videoStatusText.visibility = View.GONE
                    holder.rentButton.visibility = View.VISIBLE
                    holder.buyButton.visibility = View.VISIBLE
                    holder.deleteButton.visibility = View.GONE
                    holder.downloadButton.visibility = View.GONE
                    holder.downloadProgressBar.visibility = View.GONE
                    holder.pauseButton.visibility = View.GONE
                    holder.resumeButton.visibility = View.GONE
                } else {
                    if (video.isOwned || video.isRented) {
                        holder.rentButton.visibility = View.GONE
                        holder.buyButton.visibility = View.GONE
                        holder.pauseButton.visibility = View.GONE
                        holder.resumeButton.visibility = View.GONE
                        holder.downloadButton.visibility = View.VISIBLE

                        if (video.isOwned) {
                            holder.videoLicenseText.setText(R.string.video_download_purchased)
                        } else {
                            holder.videoLicenseText.text = String.format(
                                "Rental Expires: %s %s",
                                DateFormat.getMediumDateFormat(holder.context).format(expiryDate),
                                DateFormat.getTimeFormat(holder.context).format(expiryDate)
                            )
                        }
                    }
                }
            }
            if (status == null) {
                holder.videoStatusText.setText(R.string.checking_download_status)
                holder.videoStatusText.visibility = View.VISIBLE

                holder.video?.let {
                    catalog.getVideoDownloadStatus(
                        it,
                        object : OfflineCallback<DownloadStatus> {
                            override fun onSuccess(downloadStatus: DownloadStatus) {
                                updateDownloadStatus(holder, downloadStatus)
                            }

                            override fun onFailure(throwable: Throwable) {
                                Log.e(TAG, "Error fetching VideoDownloadStatus ", throwable)
                            }
                        })
                }
            } else {
                updateDownloadStatus(holder, status)
            }
        } else {
            holder.estimatedSizeText.visibility = View.INVISIBLE
            holder.videoLicenseText.visibility = View.GONE
            holder.videoStatusText.setText(R.string.online_only)
            holder.videoStatusText.visibility = View.VISIBLE
            holder.rentButton.visibility = View.GONE
            holder.buyButton.visibility = View.GONE
            holder.downloadButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
            holder.downloadProgressBar.visibility = View.GONE
            holder.pauseButton.visibility = View.GONE
            holder.resumeButton.visibility = View.GONE
        }

        holder.videoTitleText.text = video?.name

        val imageUri = video?.stillImageUri
        if (imageUri == null) {
            holder.videoThumbnailImage.setImageResource(R.drawable.movie)
        } else {
            Picasso.get().load(imageUri.toASCIIString()).into(holder.videoThumbnailImage)
        }

        val duration = video?.durationLong ?: -1
        if (duration > 0) {
            holder.videoDurationText.text = millisecondsToString(duration)
            holder.videoDurationText.visibility = View.VISIBLE
        } else {
            holder.videoDurationText.text = null
            holder.videoDurationText.visibility = View.GONE
        }
    }

    /**
     * Updates the view held by the specified holder with the current status of the download.
     *
     * @param holder reference to the view holder.
     * @param status optional current download status.
     */
    private fun updateDownloadStatus(holder: ViewHolder, status: DownloadStatus) {
        val statusCode = status.code

        when (statusCode) {
            DownloadStatus.STATUS_NOT_QUEUED -> {
                holder.videoStatusText.setText(R.string.press_to_save)
                holder.downloadProgressBar.visibility = View.GONE
                holder.pauseButton.visibility = View.GONE
                holder.resumeButton.visibility = View.GONE
                holder.deleteButton.visibility = View.GONE
            }

            DownloadStatus.STATUS_PENDING, DownloadStatus.STATUS_QUEUEING -> {
                holder.videoStatusText.setText(R.string.queueing_download)
                holder.downloadButton.visibility = View.GONE
                holder.downloadProgressBar.visibility = View.GONE
                holder.pauseButton.visibility = View.GONE
                holder.resumeButton.visibility = View.GONE
                holder.deleteButton.visibility = View.VISIBLE
            }

            DownloadStatus.STATUS_DOWNLOADING -> {
                holder.videoStatusText.setText(status.statusMessage)
                holder.downloadProgressBar.visibility = View.VISIBLE
                holder.downloadProgressBar.progress = status.progress.toInt()
                holder.downloadButton.visibility = View.GONE
                holder.pauseButton.visibility = View.VISIBLE
                holder.resumeButton.visibility = View.GONE
                holder.deleteButton.visibility = View.VISIBLE
            }

            DownloadStatus.STATUS_COMPLETE -> {
                holder.videoStatusText.setText(status.statusMessage)
                holder.downloadProgressBar.visibility = View.GONE
                holder.downloadProgressBar.progress = status.progress.toInt()
                holder.downloadButton.visibility = View.GONE
                holder.pauseButton.visibility = View.GONE
                holder.resumeButton.visibility = View.GONE
                holder.deleteButton.visibility = View.VISIBLE
            }

            DownloadStatus.STATUS_PAUSED -> {
                holder.videoStatusText.setText(status.statusMessage)
                holder.downloadProgressBar.visibility = View.VISIBLE
                holder.downloadProgressBar.progress = status.progress.toInt()
                holder.downloadButton.visibility = View.GONE
                holder.pauseButton.visibility = View.GONE
                holder.resumeButton.visibility = View.VISIBLE
                holder.deleteButton.visibility = View.VISIBLE
            }

            DownloadStatus.STATUS_DELETING -> {
                holder.videoLicenseText.visibility = View.GONE
                holder.rentButton.visibility = View.GONE
                holder.buyButton.visibility = View.GONE
                holder.downloadButton.visibility = View.GONE
                holder.deleteButton.visibility = View.GONE
                holder.pauseButton.visibility = View.GONE
                holder.resumeButton.visibility = View.GONE
            }

            else -> {}
        }
        holder.videoStatusText.visibility = View.VISIBLE
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            updateView(holder, position, payloads[0] as DownloadStatus)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        updateView(holder, position, null)

        // Setup listeners to handle user interactions
        holder.videoThumbnailImage.setOnClickListener {
            holder.getVideo(object : OfflineCallback<Video?> {
                override fun onSuccess(video: Video?) {
                    video?.let { listener.playVideo(it, holder.absoluteAdapterPosition) }
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e(TAG, "Error fetching the video: ", throwable)
                }
            })
        }
        holder.rentButton.setOnClickListener {
            holder.video?.let { listener.rentVideo(it) }
        }
        holder.buyButton.setOnClickListener {
            holder.video?.let { listener.buyVideo(it) }
        }
        holder.downloadButton.setOnClickListener {
            holder.video?.let { listener.downloadVideo(it) }
        }
        holder.pauseButton.setOnClickListener {
            holder.video?.let { it1 -> listener.pauseVideoDownload(it1) }
        }
        holder.resumeButton.setOnClickListener {
            holder.video?.let { it1 -> listener.resumeVideoDownload(it1) }
        }
        holder.deleteButton.setOnClickListener {
            holder.videoStatusText.text = "Deleting video..."
            holder.video?.let { it1 -> listener.deleteVideo(it1, holder.absoluteAdapterPosition) }
        }
    }

    override fun getItemCount(): Int {
        return videoList?.size ?: 0
    }

    companion object {
        private val TAG: String = VideoListAdapter::class.java.simpleName

        /**
         * Megabyte expressed in bytes.
         */
        private const val MEGABYTE_IN_BYTES = (1024 * 1024).toLong()

        /**
         * Converts the given duration into a time span string.
         *
         * @param duration elapsed time as number of milliseconds.
         * @return the formatted time span string.
         */
        private fun millisecondsToString(duration: Long): String {
            var duration = duration
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
}