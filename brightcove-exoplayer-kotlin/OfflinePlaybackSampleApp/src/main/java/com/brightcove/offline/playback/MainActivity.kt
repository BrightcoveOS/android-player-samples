package com.brightcove.offline.playback

import android.content.res.Configuration
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.brightcove.offline.playback.utils.BrightcoveDownloadUtil
import com.brightcove.offline.playback.utils.ViewUtil
import com.brightcove.player.display.ExoPlayerVideoDisplayComponent
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.CatalogError
import com.brightcove.player.edge.OfflineCallback
import com.brightcove.player.edge.OfflineCatalog
import com.brightcove.player.edge.OfflineStoreManager
import com.brightcove.player.edge.PlaylistListener
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.event.Event
import com.brightcove.player.event.EventListener
import com.brightcove.player.event.EventType
import com.brightcove.player.model.Playlist
import com.brightcove.player.model.Video
import com.brightcove.player.network.ConnectivityMonitor
import com.brightcove.player.network.DownloadStatus
import com.brightcove.player.network.HttpRequestConfig
import com.brightcove.player.offline.MediaDownloadable.DownloadEventListener
import com.brightcove.player.view.BrightcovePlayer
import java.io.Serializable
import java.util.Date
import java.util.concurrent.TimeUnit

class MainActivity : BrightcovePlayer() {
    /**
     * Specifies how long the content can be consumed after the start of playback as total number
     * of milliseconds. The default value is forty-eight hours.
     */
    val DEFAULT_RENTAL_PLAY_DURATION: Long = TimeUnit.HOURS.toMillis(48)

    /**
     * Reference to the video cloud catalog client.
     */
    private lateinit var catalog: OfflineCatalog

    /**
     * Adapter for holding the video list details.
     */
    private var videoListAdapter: VideoListAdapter? = null

    /**
     * Text view that will be used to indicate the video list type (online or offline).
     */
    private var videoListLabel: TextView? = null

    /**
     * View that will be used to display video list (online or offline).
     */
    private var videoListView: RecyclerView? = null

    /**
     * Text view that will be used to show a message when the video list is empty.
     */
    private var emptyListMessage: TextView? = null

    /**
     * Network connectivity state change monitor.
     */
    private var connectivityMonitor: ConnectivityMonitor? = null

    private lateinit var httpRequestConfig: HttpRequestConfig
    private val pasToken = "YOUR_PAS_TOKEN"
    private val PLAYDURATION_EXTENSION: Int = 10000

    private var playlist: PlaylistModel =
        PlaylistModel.byReferenceId("demo_odrm_widevine_dash", "Offline Playback List")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        onCreate()
    }

    override fun onStart() {
        super.onStart()
        ConnectivityMonitor.getInstance(this).addListener(connectivityListener)
        catalog.addDownloadEventListener(downloadEventListener)
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            ConnectivityMonitor.getInstance(this).removeListener(connectivityListener)
            catalog.removeDownloadEventListener(downloadEventListener)
        }
    }

    /**
     * Sets up the view when created.
     */
    private fun onCreate() {
        connectivityMonitor = ConnectivityMonitor.getInstance(this)
        videoListLabel = ViewUtil.findView(this, R.id.video_list_label)
        videoListView = ViewUtil.findView(this, R.id.video_list_view)
        emptyListMessage = ViewUtil.findView(this, R.id.empty_list_message)
        brightcoveVideoView = ViewUtil.findView(this, R.id.brightcove_video_view)

        val eventEmitter = brightcoveVideoView.eventEmitter

        catalog = OfflineCatalog.Builder(this, eventEmitter, getString(R.string.sdk_demo_account))
            .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
            .setPolicy(getString(R.string.sdk_demo_policy_key))
            .build()

        //Configure downloads through the catalog.
        catalog.isMobileDownloadAllowed = true
        catalog.isMeteredDownloadAllowed = false
        catalog.isRoamingDownloadAllowed = false

        videoListAdapter = VideoListAdapter(catalog, videoListListener)

        // Connect the video list view to the adapter
        val videoListView: RecyclerView = ViewUtil.findView(this, R.id.video_list_view)
        videoListView.adapter = videoListAdapter

        // Setup an adapter to render the playlist items in the spinner view Adapter that
        // will be used to bind the playlist spinner to the underlying data source.
        val playlistAdapter: ArrayAdapter<PlaylistModel> =
            ArrayAdapter(this, android.R.layout.simple_spinner_item /*, playlistNames*/)
        playlistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val videoDisplayComponent =
            brightcoveVideoView.videoDisplay as ExoPlayerVideoDisplayComponent
        videoDisplayComponent.setMediaStore(OfflineStoreManager.getInstance(this))
    }

    private fun updateVideoList() {
        videoListAdapter?.setVideoList(null)

        if (connectivityMonitor?.isConnected == true) {
            videoListLabel?.visibility = View.GONE
            videoListView?.visibility = View.GONE
            emptyListMessage?.setText(R.string.fetching_playlist)
            emptyListMessage?.visibility = View.VISIBLE

            val httpRequestConfigBuilder = HttpRequestConfig.Builder()
            httpRequestConfigBuilder.setBrightcoveAuthorizationToken(pasToken)
            httpRequestConfig = httpRequestConfigBuilder.build()
            playlist.findPlaylist(catalog, httpRequestConfig, object : PlaylistListener() {
                override fun onPlaylist(playlist: Playlist) {
                    videoListAdapter?.setVideoList(playlist.videos)
                    onVideoListUpdated(false)
                    brightcoveVideoView.addAll(playlist.videos)
                }

                override fun onError(errors: List<CatalogError>) {
                    super.onError(errors)
                }
            })
        } else {
            videoListLabel?.visibility = View.VISIBLE
            videoListLabel?.setText(R.string.offline_playlist)
            catalog.findAllVideoDownload(
                DownloadStatus.STATUS_COMPLETE,
                object : OfflineCallback<List<Video?>?> {
                    override fun onSuccess(videos: List<Video?>?) {
                        videoListAdapter?.setVideoList(videos)
                        onVideoListUpdated(false)
                        brightcoveVideoView.clear()
                        brightcoveVideoView.addAll(videos)
                    }

                    override fun onFailure(throwable: Throwable) {
                        Log.e(TAG, "Error fetching all videos downloaded: ", throwable)
                    }
                })
        }
    }

    private fun onVideoListUpdated(error: Boolean) {
        if (videoListAdapter?.itemCount == 0) {
            videoListView?.visibility = View.GONE
            if (connectivityMonitor?.isConnected == true) {
                if (error) {
                    emptyListMessage?.setText(R.string.fetching_playlist_error)
                } else {
                    emptyListMessage?.setText(R.string.fetching_playlist_no_videos)
                }
            } else {
                emptyListMessage?.setText(R.string.offline_playlist_no_videos)
            }
            emptyListMessage?.visibility = View.VISIBLE
        } else {
            videoListView!!.visibility = View.VISIBLE
            emptyListMessage!!.visibility = View.GONE
        }
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        super.onConfigurationChanged(configuration)

        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!brightcoveVideoView.isFullScreen) {
                brightcoveVideoView.eventEmitter.emit(EventType.ENTER_FULL_SCREEN)
            }
        } else if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (brightcoveVideoView.isFullScreen) {
                brightcoveVideoView.eventEmitter.emit(EventType.EXIT_FULL_SCREEN)
            }
        }
    }

    /**
     * Implements a [com.brightcove.player.offline.MediaDownloadable.DownloadEventListener] that
     * will show a toast message about the download status and refresh the video list display.
     */
    private val downloadEventListener: DownloadEventListener = object : DownloadEventListener {
        override fun onDownloadRequested(video: Video) {
            Log.i(TAG, "Starting to process ${video.name} video download request")
            videoListAdapter?.notifyVideoChanged(video)
        }

        override fun onDownloadStarted(
            video: Video,
            estimatedSize: Long,
            mediaProperties: Map<String, Serializable>
        ) {
            videoListAdapter?.notifyVideoChanged(video)
            val message = "Started to download '${video.name}' video. Estimated = ${
                Formatter.formatFileSize(
                    this@MainActivity,
                    estimatedSize
                )
            }, width = ${mediaProperties[Event.RENDITION_WIDTH]}, height = ${mediaProperties[Event.RENDITION_HEIGHT]}, mimeType = ${mediaProperties[Event.RENDITION_MIME_TYPE]}"
            showToast(message)
            Log.i(TAG, message)
        }

        override fun onDownloadProgress(video: Video, status: DownloadStatus) {
            Log.i(
                TAG, String.format(
                    "Downloaded %s out of %s of '%s' video. Progress %3.2f",
                    Formatter.formatFileSize(this@MainActivity, status.bytesDownloaded),
                    Formatter.formatFileSize(this@MainActivity, status.maxSize),
                    video.name, status.progress
                )
            )
            videoListAdapter?.notifyVideoChanged(video, status)
        }

        override fun onDownloadPaused(video: Video, status: DownloadStatus) {
            Log.i(TAG, "Paused download of '${video.name}' video: Reason #${status.reason}")
            videoListAdapter?.notifyVideoChanged(video, status)
        }

        override fun onDownloadCompleted(video: Video, status: DownloadStatus) {
            videoListAdapter?.notifyVideoChanged(video, status)
            val message = "Successfully saved '${video.name}' video"
            showToast(message)
            Log.i(TAG, message)
        }

        override fun onDownloadCanceled(video: Video) {
            //No need to update UI here because it will be handled by the deleteVideo method.
            val message = "Cancelled download of '${video.name}' video removed"
            showToast(message)
            Log.i(TAG, message)
            onDownloadRemoved(video)
        }

        override fun onDownloadDeleted(video: Video) {
            //No need to update UI here because it will be handled by the deleteVideo method.
            val message = "Offline copy of '${video.name}' video removed"
            showToast(message)
            Log.i(TAG, message)
            onDownloadRemoved(video)
        }

        override fun onDownloadFailed(video: Video, status: DownloadStatus) {
            videoListAdapter?.notifyVideoChanged(video, status)
            val message = "Failed to download '${video.name}' video: Error #${status.reason}"
            showToast(message)
            Log.e(TAG, message)
        }
    }

    /**
     * Called when an offline copy of the video is either cancelled or deleted.
     *
     * @param video the video that was removed.
     */
    private fun onDownloadRemoved(video: Video) {
        if (connectivityMonitor?.isConnected == true) {
            // Fetch the video object again to avoid using the given video that may have been
            // tainted by previous download.
            catalog.findVideoByID(video.id, object : FindVideoListener(video) {
                override fun onVideo(newVideo: Video) {
                    videoListAdapter?.notifyVideoChanged(newVideo)
                }
            })
        } else {
            videoListAdapter?.removeVideo(video)
            onVideoListUpdated(false)
        }
    }

    /**
     * Implements a [com.brightcove.player.network.ConnectivityMonitor.Listener] that will
     * update the current video list based on network connectivity state.
     */
    private val connectivityListener = ConnectivityMonitor.Listener { _, _ -> updateVideoList() }

    /**
     * Implements a [VideoListListener] that responds to user interaction on the video list.
     */
    private val videoListListener: VideoListListener =
        object : VideoListListener {
            override fun playVideo(video: Video, videoIndex: Int) {
                this@MainActivity.playVideo(video, videoIndex)
            }

            override fun rentVideo(video: Video) {
                // Fetch the video object again to avoid using the given video that may have been
                // changed by previous download.
                catalog.findVideoByID(video.id, object : FindVideoListener(video) {
                    override fun onVideo(newVideo: Video) {
                        this@MainActivity.rentVideo(newVideo)
                    }
                })
            }

            override fun buyVideo(video: Video) {
                // Fetch the video object again to avoid using the given video that may have been
                // changed by previous download.
                catalog.findVideoByID(video.id, object : FindVideoListener(video) {
                    override fun onVideo(newVideo: Video) {
                        val httpRequestConfigBuilder = HttpRequestConfig.Builder()
                        httpRequestConfigBuilder.setBrightcoveAuthorizationToken(pasToken)
                        httpRequestConfig = httpRequestConfigBuilder.build()
                        catalog.requestPurchaseLicense(
                            video,
                            licenseEventListener,
                            httpRequestConfig
                        )
                    }
                })
            }

            override fun pauseVideoDownload(video: Video) {
                Log.v(TAG, "Calling pauseVideoDownload.")
                catalog.pauseVideoDownload(video, object : OfflineCallback<Int?> {
                    override fun onSuccess(integer: Int?) {
                        // Video download was paused successfully
                    }

                    override fun onFailure(throwable: Throwable) {
                        Log.e(TAG, "Error pausing video download: ", throwable)
                    }
                })
            }

            override fun resumeVideoDownload(video: Video) {
                Log.v(TAG, "Calling resumeVideoDownload.")
                catalog.resumeVideoDownload(video, object : OfflineCallback<Int?> {
                    override fun onSuccess(integer: Int?) {
                        // Video download was resumed successfully
                    }

                    override fun onFailure(throwable: Throwable) {
                        Log.e(TAG, "Error resuming video download: ", throwable)
                    }
                })
            }

            override fun downloadVideo(video: Video) {
                // bundle has all available captions and audio tracks
                catalog.getMediaFormatTracksAvailable(video) { mediaDownloadable, bundle ->
                    BrightcoveDownloadUtil.selectMediaFormatTracksAvailable(
                        mediaDownloadable,
                        bundle
                    )
                    catalog.downloadVideo(video, object : OfflineCallback<DownloadStatus?> {
                        override fun onSuccess(downloadStatus: DownloadStatus?) {
                            // Video download started successfully
                            videoListAdapter?.notifyVideoChanged(video)
                        }

                        override fun onFailure(throwable: Throwable) {
                            Log.e(TAG, "Error initializing video download: ", throwable)
                        }
                    })
                }
            }

            override fun deleteVideo(video: Video, videoIndex: Int) {
                catalog.deleteVideo(video, object : OfflineCallback<Boolean?> {
                    override fun onSuccess(aBoolean: Boolean?) {
                        if (connectivityMonitor?.isConnected != true) {
                            brightcoveVideoView.remove(videoIndex)
                        }
                    }

                    override fun onFailure(throwable: Throwable) {
                        Log.e(TAG, "Error deleting video: ", throwable)
                    }
                })
            }
        }

    /**
     * Plays the specified video. If the player is already playing a video, playback will be stopped
     * and the current video cleared before loading the new video into the player.
     *
     * @param video the video to be played.
     */
    private fun playVideo(video: Video, videoIndex: Int) {
        brightcoveVideoView.replace(videoIndex, video)
        brightcoveVideoView.currentIndex = videoIndex
        brightcoveVideoView.start()
    }

    private val licenseEventListener =
        EventListener { event ->
            val type = event.type
            val video = event.properties[Event.VIDEO] as Video?
            when (type) {
                EventType.ODRM_LICENSE_ACQUIRED -> {
                    video?.let { videoListAdapter?.notifyVideoChanged(it) }
                    val message = "Successfully downloaded license for '${video?.name}' video"
                    showToast(message)
                    Log.i(TAG, message)
                }

                EventType.ODRM_PLAYBACK_NOT_ALLOWED, EventType.ODRM_SOURCE_NOT_FOUND -> {
                    val message = "Failed to downloaded license for '${video?.name}' video: $type"
                    showToast(message)
                    Log.w(TAG, message)
                }

                EventType.ODRM_LICENSE_ERROR -> {
                    val message =
                        "Error encountered while downloading license for '${video?.name}' video"
                    showToast(message)
                    Log.e(TAG, message, event.properties[Event.ERROR] as Throwable?)
                }
            }
        }

    /**
     * Attempts to download an offline playback rental license for the specified video. Upon
     * successful acquisition of offline playback license, the video can be downloaded
     * into the host device.
     *
     * @param video the video for which the offline playback license is needed.
     */
    private fun rentVideo(video: Video) {
        DatePickerFragment().setTitle("Select Rental Expiry Date").setListener(
            object : DatePickerFragment.Listener {
                override fun onDateSelected(expiryDate: Date) {
                    // Use the default rental play duration for offline playback license.
                    // This determines how long the content can be consumed after download,
                    // not the actual video duration.
                    val playDuration: Long = DEFAULT_RENTAL_PLAY_DURATION

                    val httpRequestConfigBuilder = HttpRequestConfig.Builder()
                    httpRequestConfigBuilder.setBrightcoveAuthorizationToken(pasToken)
                    httpRequestConfig = httpRequestConfigBuilder.build()
                    catalog.requestRentalLicense(
                        video,
                        expiryDate,
                        playDuration,
                        licenseEventListener,
                        httpRequestConfig
                    )
                }
            })
            .show(fragmentManager, "rentalExpiryDatePicker")
    }

    /**
     * Shows a formatted toast message.
     *
     * @param message    the message to be shown.
     */
    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Find video listener extends the base implementation to show toast if the video is not found.
     */
    private abstract class FindVideoListener
    /**
     * Constructs a new find video listener
     *
     * @param video the video being searched.
     */(
        /**
         * The video being retrieved.
         */
        private val video: Video
    ) :
        VideoListener() {
        override fun onError(errors: List<CatalogError>) {
            super.onError(errors)
        }
    }
}