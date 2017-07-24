package com.brightcove.player.samples.offlineplayback.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Provides utility methods to work with Android {@link android.view.View}.
 *
 * @author rsubramaniam (rsubramaniam@brightcove.com)
 */
public final class ViewUtil {

    /**
     * Prevents contruction of this utility class.
     */
    private ViewUtil() {
        // Nothing to do
    }

    /**
     * Searches the parent view for a child view with the specified unique resource identifier.
     *
     * @param parent reference to the parent view to be searched.
     * @param id     the unique resource identifier of the child to be found.
     * @param <V>    expected type of the child view.
     * @return reference to the child view.
     * @throws IllegalStateException if the child is not found.
     */
    @NonNull
    public static <V extends View> V findView(@NonNull View parent, int id) {
        V child = (V) parent.findViewById(id);
        if (child == null) {
            String resourceName = parent.getResources().getResourceName(id);
            throw new IllegalStateException("Could not find resource: " + resourceName);
        }

        return child;
    }

    /**
     * Searches the activity for a child view with the specified unique resource identifier.
     *
     * @param parent reference to the activity to be searched.
     * @param id     the unique resource identifier of the child to be found.
     * @param <V>    expected type of the child view.
     * @return reference to the child view.
     * @throws IllegalStateException if the child is not found.
     */
    @NonNull
    public static <V extends View> V findView(@NonNull Activity parent, int id) {
        V child = (V) parent.findViewById(id);
        if (child == null) {
            String resourceName = parent.getResources().getResourceName(id);
            throw new IllegalStateException("Could not find resource: " + resourceName);
        }

        return child;
    }

}