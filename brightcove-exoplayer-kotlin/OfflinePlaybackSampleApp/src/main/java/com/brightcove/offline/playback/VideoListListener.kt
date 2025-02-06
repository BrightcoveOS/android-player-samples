package com.brightcove.offline.playback

import com.brightcove.player.model.Video


/**
 * The contract of listener that can handle user interactions on a video item display.
 */
interface VideoListListener {
    /**
     * This method will be called by the [VideoListAdapter] when the user touches the
     * video thumbnail. The handler is expected to play the specified video.
     *
     * @param video reference to the video.
     * @param absoluteAdapterPosition
     */
    fun playVideo(video: Video, absoluteAdapterPosition: Int)

    /**
     * This method will be called by the [VideoListAdapter] when the user touches the
     * Rent button. The handler is expected to initiate download of the specified video.
     *
     * @param video reference to the video.
     */
    fun rentVideo(video: Video)

    /**
     * This method will be called by the [VideoListAdapter] when the user touches the
     * Buy button. The handler is expected to initiate download of the specified video.
     *
     * @param video reference to the video.
     */
    fun buyVideo(video: Video)

    /**
     * This method will be called by the [VideoListAdapter] when the user touches the
     * Download button. The handler is expected to initiate download of the specified video.
     *
     * @param video reference to the video.
     */
    fun downloadVideo(video: Video)

    /**
     * This method will be called by the [VideoListAdapter] when the user touches the
     * pause download button. The handler is expected to pause download of the specified video.
     *
     * @param video reference to the video.
     */
    fun pauseVideoDownload(video: Video)

    /**
     * This method will be called by the [VideoListAdapter] when the user touches the
     * resume download button. The handler is expected to resume download of the specified video.
     *
     * @param video reference to the video.
     */
    fun resumeVideoDownload(video: Video)

    /**
     * This method will be called by the [VideoListAdapter] when the user touches the
     * Delete button. The handler is expected to delete the specified video.
     *
     * @param video reference to the video.
     * @param absoluteAdapterPosition
     */
    fun deleteVideo(video: Video, absoluteAdapterPosition: Int)
}