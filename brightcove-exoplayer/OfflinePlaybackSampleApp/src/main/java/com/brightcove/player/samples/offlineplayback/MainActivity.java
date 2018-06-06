package com.brightcove.player.samples.offlineplayback;

import android.content.res.Configuration;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.brightcove.player.edge.OfflineCatalog;
import com.brightcove.player.edge.PlaylistListener;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.model.Video;
import com.brightcove.player.network.ConnectivityMonitor;
import com.brightcove.player.network.DownloadStatus;
import com.brightcove.player.offline.MediaDownloadable;
import com.brightcove.player.samples.offlineplayback.utils.BrightcoveDownloadUtil;
import com.brightcove.player.samples.offlineplayback.utils.ViewUtil;
import com.brightcove.player.view.BrightcovePlayer;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * An activity that displays a list of videos that can be downloaded.
 */
public class MainActivity extends BrightcovePlayer {
    /**
     * The name that will be used to tag the events generated by this class.
     */
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * The video cloud account identifier.
     */
    private static final String ACCOUNT_ID = "5420904993001";

    /**
     * The policy key for the video cloud account.
     */
    private static final String POLICY_KEY = "BCpkADawqM1RJu5c_I13hBUAi4c8QNWO5QN2yrd_OgDjTCVsbILeGDxbYy6xhZESTFi68MiSUHzMbQbuLV3q-gvZkJFpym1qYbEwogOqKCXK622KNLPF92tX8AY9a1cVVYCgxSPN12pPAuIM";

    /**
     * Specifies how long the content can be consumed after the start of playback as total number
     * of milliseconds. The default value is forty-eight hours.
     */
    private static long DEFAULT_RENTAL_PLAY_DURATION = TimeUnit.HOURS.toMillis(48);

    /**
     * Reference to the video cloud catalog client.
     */
    private OfflineCatalog catalog;

    /**
     * Adapter for holding the video list details.
     */
    private VideoListAdapter videoListAdapter;

    /**
     * Text view that will be used to indicate the video list type (online or offline).
     */
    private TextView videoListLabel;

    /**
     * View that will be used to display video list (online or offline).
     */
    private RecyclerView videoListView;

    /**
     * Text view that will be used to show a message when the video list is empty.
     */
    private TextView emptyListMessage;

    /**
     * Network connectivity state change monitor.
     */
    private ConnectivityMonitor connectivityMonitor;

    PlaylistModel playlist = PlaylistModel.byReferenceId("demo_odrm_widevine_dash", "Offline Playback List");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectivityMonitor.getInstance(this).addListener(connectivityListener);
        catalog.addDownloadEventListener(downloadEventListener);
        updateVideoList();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing()) {
            ConnectivityMonitor.getInstance(this).removeListener(connectivityListener);
            catalog.removeDownloadEventListener(downloadEventListener);
        }
    }

    /**
     * Sets up the view when created.
     */
    private void onCreate() {
        connectivityMonitor = ConnectivityMonitor.getInstance(this);
        videoListLabel = ViewUtil.findView(this, R.id.video_list_label);
        videoListView = ViewUtil.findView(this, R.id.video_list_view);
        emptyListMessage = ViewUtil.findView(this, R.id.empty_list_message);

        brightcoveVideoView = ViewUtil.findView(this, R.id.brightcove_video_view);
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();
        catalog = new OfflineCatalog(this, eventEmitter, ACCOUNT_ID, POLICY_KEY);

        //Configure downloads through the catalog.
        catalog.setMobileDownloadAllowed(true);
        catalog.setMeteredDownloadAllowed(false);
        catalog.setRoamingDownloadAllowed(false);

        videoListAdapter = new VideoListAdapter(catalog, videoListListener);

        // Connect the video list view to the adapter
        RecyclerView videoListView = ViewUtil.findView(this, R.id.video_list_view);
        videoListView.setAdapter(videoListAdapter);

        // Setup an adapter to render the playlist items in the spinner view Adapter that
        // will be used to bind the playlist spinner to the underlying data source.
        ArrayAdapter<PlaylistModel> playlistAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item/*, playlistNames*/);
        playlistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void updateVideoList() {
        videoListAdapter.setVideoList(null);

        if (connectivityMonitor.isConnected()) {
            videoListLabel.setVisibility(View.GONE);
            videoListView.setVisibility(View.GONE);
            emptyListMessage.setText(R.string.fetching_playlist);
            emptyListMessage.setVisibility(View.VISIBLE);

            playlist.findPlaylist(catalog, new PlaylistListener() {
                @Override
                public void onPlaylist(Playlist playlist) {
                    videoListAdapter.setVideoList(playlist.getVideos());
                    onVideoListUpdated(false);
                }

                @Override
                public void onError(String error) {
                    String message = showToast("Failed to find playlist[%s]: %s", playlist.displayName, error);
                    Log.w(TAG, message);
                    onVideoListUpdated(true);
                }
            });
        } else {
            videoListLabel.setVisibility(View.VISIBLE);
            videoListLabel.setText(R.string.offline_playlist);
            List<Video> videoList = catalog.findAllVideoDownload(DownloadStatus.STATUS_COMPLETE);
            videoListAdapter.setVideoList(videoList);
            onVideoListUpdated(false);
        }
    }

    private void onVideoListUpdated(boolean error) {
        if (videoListAdapter.getItemCount() == 0) {
            videoListView.setVisibility(View.GONE);
            if (connectivityMonitor.isConnected()) {
                if (error) {
                    emptyListMessage.setText(R.string.fetching_playlist_error);
                } else {
                    emptyListMessage.setText(R.string.fetching_playlist_no_videos);
                }
            } else {
                emptyListMessage.setText(R.string.offline_playlist_no_videos);
            }
            emptyListMessage.setVisibility(View.VISIBLE);
        } else {
            videoListView.setVisibility(View.VISIBLE);
            emptyListMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);

        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!brightcoveVideoView.isFullScreen()) {
                brightcoveVideoView.getEventEmitter().emit(EventType.ENTER_FULL_SCREEN);
            }
        } else if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (brightcoveVideoView.isFullScreen()) {
                brightcoveVideoView.getEventEmitter().emit(EventType.EXIT_FULL_SCREEN);
            }
        }
    }

    /**
     * Implements a {@link com.brightcove.player.offline.MediaDownloadable.DownloadEventListener} that
     * will show a toast message about the download status and refresh the video list display.
     */
    private final MediaDownloadable.DownloadEventListener downloadEventListener = new MediaDownloadable.DownloadEventListener() {

        @Override
        public void onDownloadRequested(@NonNull final Video video) {
            Log.i(TAG, String.format(
                    "Starting to process '%s' video download request", video.getName()));
            videoListAdapter.notifyVideoChanged(video);
        }

        @Override
        public void onDownloadStarted(@NonNull Video video, long estimatedSize, @NonNull Map<String, Serializable> mediaProperties) {
            videoListAdapter.notifyVideoChanged(video);
            String message = showToast(
                    "Started to download '%s' video. Estimated = %s, width = %s, height = %s, mimeType = %s",
                    video.getName(),
                    Formatter.formatFileSize(MainActivity.this, estimatedSize),
                    mediaProperties.get(Event.RENDITION_WIDTH),
                    mediaProperties.get(Event.RENDITION_HEIGHT),
                    mediaProperties.get(Event.RENDITION_MIME_TYPE)
            );
            Log.i(TAG, message);
        }

        @Override
        public void onDownloadProgress(@NonNull final Video video, @NonNull final DownloadStatus status) {
            Log.i(TAG, String.format(
                    "Downloaded %s out of %s of '%s' video. Progress %3.2f",
                    Formatter.formatFileSize(MainActivity.this, status.getBytesDownloaded()),
                    Formatter.formatFileSize(MainActivity.this, status.getMaxSize()),
                    video.getName(), status.getProgress()));
            videoListAdapter.notifyVideoChanged(video, status);
        }

        @Override
        public void onDownloadPaused(@NonNull final Video video, @NonNull final DownloadStatus status) {
            Log.i(TAG, String.format(
                    "Paused download of '%s' video: Reason #%d", video.getName(), status.getReason()));
            videoListAdapter.notifyVideoChanged(video, status);
        }

        @Override
        public void onDownloadCompleted(@NonNull final Video video, @NonNull final DownloadStatus status) {
            videoListAdapter.notifyVideoChanged(video, status);
            String message = showToast(
                    "Successfully saved '%s' video", video.getName());
            Log.i(TAG, message);
        }

        @Override
        public void onDownloadCanceled(@NonNull final Video video) {
            //No need to update UI here because it will be handled by the deleteVideo method.
            String message = showToast(
                    "Cancelled download of '%s' video removed", video.getName());
            Log.i(TAG, message);
            onDownloadRemoved(video);
        }

        @Override
        public void onDownloadDeleted(@NonNull final Video video) {
            //No need to update UI here because it will be handled by the deleteVideo method.
            String message = showToast(
                    "Offline copy of '%s' video removed", video.getName());
            Log.i(TAG, message);
            onDownloadRemoved(video);
        }

        @Override
        public void onDownloadFailed(@NonNull final Video video, @NonNull final DownloadStatus status) {
            videoListAdapter.notifyVideoChanged(video, status);
            String message = showToast(
                    "Failed to download '%s' video: Error #%d", video.getName(), status.getReason());
            Log.e(TAG, message);
        }
    };

    /**
     * Called when an offline copy of the video is either cancelled or deleted.
     *
     * @param video the video that was removed.
     */
    private void onDownloadRemoved(@NonNull final Video video) {
        if (connectivityMonitor.isConnected()) {
            // Fetch the video object again to avoid using the given video that may have been
            // tainted by previous download.
            catalog.findVideoByID(video.getId(), new FindVideoListener(video) {
                @Override
                public void onVideo(Video newVideo) {
                    videoListAdapter.notifyVideoChanged(newVideo);
                }
            });
        } else {
            videoListAdapter.removeVideo(video);
            onVideoListUpdated(false);
        }
    }

    /**
     * Implements a {@link com.brightcove.player.network.ConnectivityMonitor.Listener} that will
     * update the current video list based on network connectivity state.
     */
    private final ConnectivityMonitor.Listener connectivityListener = new ConnectivityMonitor.Listener() {
        public void onConnectivityChanged(boolean connected, @Nullable NetworkInfo networkInfo) {
            updateVideoList();
        }
    };

    /**
     * Implements a {@link VideoListListener} that responds to user interaction on the video list.
     */
    private final VideoListListener videoListListener = new VideoListListener() {
        @Override
        public void playVideo(@NonNull Video video) {
            MainActivity.this.playVideo(video);
        }

        @Override
        public void rentVideo(@NonNull final Video video) {
            // Fetch the video object again to avoid using the given video that may have been
            // changed by previous download.
            catalog.findVideoByID(video.getId(), new FindVideoListener(video) {
                @Override
                public void onVideo(Video newVideo) {
                    MainActivity.this.rentVideo(newVideo);
                }
            });
        }

        @Override
        public void buyVideo(@NonNull final Video video) {
            // Fetch the video object again to avoid using the given video that may have been
            // changed by previous download.
            catalog.findVideoByID(video.getId(), new FindVideoListener(video) {
                @Override
                public void onVideo(Video newVideo) {
                    catalog.requestPurchaseLicense(video, licenseEventListener);
                }
            });
        }

        @Override
        public void pauseVideoDownload(@NonNull Video video) {
            Log.v(TAG,"Calling pauseVideoDownload.");
            catalog.pauseVideoDownload(video); }

        @Override
        public void resumeVideoDownload(@NonNull Video video) {
            Log.v(TAG,"Calling resumeVideoDownload.");
            catalog.resumeVideoDownload(video); }

        @Override
        public void downloadVideo(@NonNull final Video video) {
            // bundle has all available captions and audio tracks
            catalog.getMediaFormatTracksAvailable(video, new MediaDownloadable.MediaFormatListener() {
                @Override
                public void onResult(MediaDownloadable mediaDownloadable, Bundle bundle) {
                    BrightcoveDownloadUtil.selectMediaFormatTracksAvailable(
                            video, mediaDownloadable, bundle);
                    try {
                        catalog.downloadVideo(video);
                    } catch (IllegalStateException iSE) {
                        android.util.Log.w(TAG, "Exception when downloading video " + video.getId(), iSE);
                    }
                }
            });
        }

        @Override
        public void deleteVideo(@NonNull Video video) {
            catalog.deleteVideo(video);
        }
    };

    /**
     * Plays the specified video. If the player is already playing a video, playback will be stopped
     * and the current video cleared before loading the new video into the player.
     *
     * @param video the video to be played.
     */
    private void playVideo(@NonNull Video video) {
        brightcoveVideoView.stopPlayback();
        brightcoveVideoView.clear();
        brightcoveVideoView.add(video);
        brightcoveVideoView.start();
    }

    private final EventListener licenseEventListener = new EventListener() {
        @Override
        public void processEvent(Event event) {

            final String type = event.getType();
            final Video video = (Video) event.properties.get(Event.VIDEO);

            switch (type) {
                case EventType.ODRM_LICENSE_ACQUIRED: {
                    videoListAdapter.notifyVideoChanged(video);
                    String message = showToast(
                            "Successfully downloaded license for '%s' video", video.getName());
                    Log.i(TAG, message);
                    break;
                }
                case EventType.ODRM_PLAYBACK_NOT_ALLOWED:
                case EventType.ODRM_SOURCE_NOT_FOUND: {
                    String message = showToast(
                            "Failed to downloaded license for '%s' video: %s", video.getName(), type);
                    Log.w(TAG, message);
                    break;
                }
                case EventType.ODRM_LICENSE_ERROR: {
                    String message = showToast(
                            "Error encountered while downloading license for '%s' video", video.getName());
                    Log.e(TAG, message, (Throwable) event.properties.get(Event.ERROR));
                    break;
                }
            }
        }
    };

    /**
     * Attempts to download an offline playback rental license for the specified video. Upon
     * successful acquisition of offline playback license, the video can be downloaded
     * into the host device.
     *
     * @param video the video for which the offline playback license is needed.
     */
    private void rentVideo(@NonNull final Video video) {
        new DatePickerFragment()
                .setTitle("Select Rental Expiry Date")
                .setListener(new DatePickerFragment.Listener() {
                    @Override
                    public void onDateSelected(@NonNull Date expiryDate) {
                        long playDuration = video.getDuration();
                        if (playDuration == 0) {
                            playDuration = DEFAULT_RENTAL_PLAY_DURATION;
                        }
                        catalog.requestRentalLicense(video, expiryDate, playDuration, licenseEventListener);
                    }
                })
                .show(getFragmentManager(), "rentalExpiryDatePicker");
    }

    /**
     * Shows a formatted toast message.
     *
     * @param message    the message to be shown. The message may include string format tokens.
     * @param parameters the parameters to be used for formatting the message.
     * @return the formatted message that was shown.
     * @see String#format(String, Object...)
     */
    private String showToast(@NonNull String message, @Nullable Object... parameters) {
        if (parameters != null) {
            message = String.format(message, parameters);
        }
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

        return message;
    }

    /**
     * Find video listener extends the base implementation to show toast if the video is not found.
     */
    private abstract class FindVideoListener extends VideoListener {
        /**
         * The video being retrieved.
         */
        private final Video video;

        /**
         * Constructs a new find video listener
         *
         * @param video the video being searched.
         */
        public FindVideoListener(Video video) {
            this.video = video;
        }

        @Override
        public void onError(String error) {
            String message = showToast(
                    "Cannot find '%s' video: %s", video.getName(), error);
            Log.e(TAG, message);
        }
    }

}
