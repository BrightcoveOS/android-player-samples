package com.brightcove.offline.playback.utils

import android.app.Activity
import android.view.View

/**
 * Provides utility methods to work with Android [View].
 *
 * @author rsubramaniam (rsubramaniam@brightcove.com)
 */
object ViewUtil {
    /**
     * Searches the parent view for a child view with the specified unique resource identifier.
     *
     * @param parent reference to the parent view to be searched.
     * @param id     the unique resource identifier of the child to be found.
     * @param <V>    expected type of the child view.
     * @return reference to the child view.
     * @throws IllegalStateException if the child is not found.
    </V> */
    fun <V : View?> findView(parent: View, id: Int): V {
        val child = parent.findViewById<View>(id) as? V
        if (child == null) {
            val resourceName = parent.resources.getResourceName(id)
            throw IllegalStateException("Could not find resource: $resourceName")
        }
        return child
    }

    /**
     * Searches the activity for a child view with the specified unique resource identifier.
     *
     * @param parent reference to the activity to be searched.
     * @param id     the unique resource identifier of the child to be found.
     * @param <V>    expected type of the child view.
     * @return reference to the child view.
     * @throws IllegalStateException if the child is not found.
    </V> */
    fun <V : View?> findView(parent: Activity, id: Int): V {
        val child = parent.findViewById<View>(id) as? V
        if (child == null) {
            val resourceName = parent.resources.getResourceName(id)
            throw IllegalStateException("Could not find resource: $resourceName")
        }
        return child
    }
}