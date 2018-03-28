package com.brightcove.player.samples.offlineplayback;


import android.support.annotation.NonNull;

import com.brightcove.player.model.Video;

/**
 * The contract of listener that can handle user interactions on a video item display.
 */
public interface VideoListListener {

    /**
     * This method will be called by the {@link VideoListAdapter} when the user touches the
     * video thumbnail. The handler is expected to play the specified video.
     *
     * @param video reference to the video.
     */
    void playVideo(@NonNull Video video);

    /**
     * This method will be called by the {@link VideoListAdapter} when the user touches the
     * Rent button. The handler is expected to initiate download of the specified video.
     *
     * @param video reference to the video.
     */
    void rentVideo(@NonNull Video video);

    /**
     * This method will be called by the {@link VideoListAdapter} when the user touches the
     * Buy button. The handler is expected to initiate download of the specified video.
     *
     * @param video reference to the video.
     */
    void buyVideo(@NonNull Video video);

    /**
     * This method will be called by the {@link VideoListAdapter} when the user touches the
     * Download button. The handler is expected to initiate download of the specified video.
     *
     * @param video reference to the video.
     */
    void downloadVideo(@NonNull Video video);

    /**
     * This method will be called by the {@link VideoListAdapter} when the user touches the
     * pause download button. The handler is expected to pause download of the specified video.
     *
     * @param video reference to the video.
     */
    void pauseVideoDownload(@NonNull Video video);

    /**
     * This method will be called by the {@link VideoListAdapter} when the user touches the
     * resume download button. The handler is expected to resume download of the specified video.
     *
     * @param video reference to the video.
     */
    void resumeVideoDownload(@NonNull Video video);

    /**
     * This method will be called by the {@link VideoListAdapter} when the user touches the
     * Delete button. The handler is expected to delete the specified video.
     *
     * @param video reference to the video.
     */
    void deleteVideo(@NonNull Video video);
}