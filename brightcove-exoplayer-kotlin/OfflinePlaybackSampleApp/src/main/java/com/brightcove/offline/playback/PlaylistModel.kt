package com.brightcove.offline.playback

import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.PlaylistListener
import com.brightcove.player.network.HttpRequestConfig

/**
 * Playlist model is an immutable DTO that holds important values related to a Playlist.
 */
class PlaylistModel(id: String?, referenceId: String?, displayName: String) {
    /**
     * The unique primary identifier of the playlist on the video cloud. This value may change
     * during server side maintenance. Please use [.referenceId] instead.
     */
    private val id: String?

    /**
     * The unique reference identifier of the playlist on the video cloud as set by the cloud account owner.
     */
    private val referenceId: String?

    /**
     * The display name of the playlist.
     */
    private val displayName: String

    /**
     * Constructs a new playlist model.
     *
     * @param id          The unique primary identifier of the playlist on the video cloud.
     * @param referenceId The unique reference identifier of the playlist on the video cloud.
     * @param displayName The display name of the playlist.
     * @throws IllegalArgumentException if both [.id] and [.referenceId] are null.
     * @throws IllegalArgumentException if the playlist display name is null.
     */
    init {
        require(!(id == null && referenceId == null)) { "Playlist must have a non-null identifier or reference identifier" }

        this.id = id
        this.referenceId = referenceId
        this.displayName = displayName
    }

    /**
     * Overrides the base implementation to return the playlist display name.
     *
     * @return the playlist name.
     */
    override fun toString(): String {
        return displayName
    }

    /**
     * Searches the given catalog for this playlist.
     *
     * @param catalog  reference to the catalog to be searched.
     * @param listener reference to a listener instance that will be notified when the search is complete.
     * @throws NullPointerException if the catalog or listener is null.
     */
    fun findPlaylist(
        catalog: Catalog,
        httpRequestConfig: HttpRequestConfig,
        listener: PlaylistListener
    ) {
        if (referenceId != null) {
            catalog.findPlaylistByReferenceID(referenceId, httpRequestConfig, listener)
        } else if (id != null) {
            catalog.findPlaylistByID(id, httpRequestConfig, listener)
        }
    }

    companion object {
        /**
         * Creates a playlist model with the given unique primary identifier and display name.
         *
         * @param id   The unique primary identifier of the playlist on the video cloud.
         * @param displayName The display name of the playlist.
         * @return reference to the newly created playlist model.
         */
        fun byId(id: String, displayName: String): PlaylistModel {
            return PlaylistModel(id, null, displayName)
        }

        /**
         * Creates a playlist model with the given unique reference identifier and displayName.
         *
         * @param referenceId The unique reference identifier of the playlist on the video cloud.
         * @param displayName        The display name of the playlist.
         * @return reference to the newly created playlist model.
         */
        fun byReferenceId(referenceId: String, displayName: String): PlaylistModel {
            return PlaylistModel(null, referenceId, displayName)
        }
    }
}
