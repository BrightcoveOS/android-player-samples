package com.brightcove.player.samples.offlineplayback;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.PlaylistListener;

/**
 * Playlist model is an immutable DTO that holds important values related to a Playlist.
 */
public class PlaylistModel {
    /**
     * The unique primary identifier of the playlist on the video cloud. This value may change
     * during server side maintenance. Please use {@link #referenceId} instead.
     */
    public final String id;

    /**
     * The unique reference identifier of the playlist on the video cloud as set by the cloud account owner.
     */
    public final String referenceId;

    /**
     * The display name of the playlist.
     */
    public final String displayName;

    /**
     * Constructs a new playlist model.
     *
     * @param id          The unique primary identifier of the playlist on the video cloud.
     * @param referenceId The unique reference identifier of the playlist on the video cloud.
     * @param displayName The display name of the playlist.
     * @throws IllegalArgumentException if both {@link #id} and {@link #referenceId} are null.
     * @throws IllegalArgumentException if the playlist display name is null.
     */
    public PlaylistModel(@Nullable String id, @Nullable String referenceId, @NonNull String displayName) {
        if (id == null && referenceId == null) {
            throw new IllegalArgumentException("Playlist must have a non-null identifier or reference identifier");
        }

        if (displayName == null) {
            throw new IllegalArgumentException("Playlist displayName is null");
        }

        this.id = id;
        this.referenceId = referenceId;
        this.displayName = displayName;
    }

    /**
     * Overrides the base implementation to return the playlist display name.
     *
     * @return the playlist name.
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Creates a playlist model with the given unique primary identifier and display name.
     *
     * @param id   The unique primary identifier of the playlist on the video cloud.
     * @param displayName The display name of the playlist.
     * @return reference to the newly created playlist model.
     */
    public static PlaylistModel byId(@NonNull String id, @NonNull String displayName) {
        return new PlaylistModel(id, null, displayName);
    }

    /**
     * Creates a playlist model with the given unique reference identifier and displayName.
     *
     * @param referenceId The unique reference identifier of the playlist on the video cloud.
     * @param displayName        The display name of the playlist.
     * @return reference to the newly created playlist model.
     */
    public static PlaylistModel byReferenceId(@NonNull String referenceId, @NonNull String displayName) {
        return new PlaylistModel(null, referenceId, displayName);
    }

    /**
     * Searches the given catalog for this playlist.
     *
     * @param catalog  reference to the catalog to be searched.
     * @param listener reference to a listener instance that will be notified when the search is complete.
     * @throws NullPointerException if the catalog or listener is null.
     */
    public void findPlaylist(@NonNull Catalog catalog, @NonNull PlaylistListener listener) {
        if (referenceId != null) {
            catalog.findPlaylistByReferenceID(referenceId, listener);
        } else if (id != null) {
            catalog.findPlaylistByID(id, listener);
        }
    }
}
