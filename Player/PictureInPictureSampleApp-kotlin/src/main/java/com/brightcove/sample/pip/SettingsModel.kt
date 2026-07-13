package com.brightcove.sample.pip

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Rational
import androidx.preference.PreferenceManager
import com.brightcove.player.logging.Log

class SettingsModel(context: Context) {

    private var preferences: SharedPreferences

    init {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun isPictureInPictureClosedCaptionsEnabled(): Boolean {
        val defaultValue = false
        return preferences.getBoolean(PIP_CC_ENABLED, defaultValue)
    }

    fun isPictureInPictureOnUserLeaveEnabled(): Boolean {
        return preferences.getBoolean(PIP_ON_USER_LEAVE_ENABLED, false)
    }

    fun getPictureInPictureCCScaleFactor(): Float {
        var scaleFactor = 0.5f
        val scaleFactorString = preferences.getString(PIP_CC_SCALE_FACTOR, scaleFactor.toString())
        try {
            scaleFactor = scaleFactorString!!.toFloat()
        } catch (e: NumberFormatException) {
            Log.e(TAG, "Error retrieving bitrate configuration.", e)
        }
        return scaleFactor
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun getPictureInPictureAspectRatio(): Rational? {
        var rational: Rational? = null
        val numerator: Int
        val denominator: Int
        val rationalString = preferences.getString(PIP_ASPECT_RATIO, null)
        try {
            if (rationalString != null && rationalString.contains(":")) {
                val numbers = rationalString.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                numerator = numbers[0].toInt()
                denominator = numbers[1].toInt()
                Log.w("PIP", String.format("RATIONAL N=%s, D=%s", numerator, denominator))
                rational = Rational(numerator, denominator)
            }
        } catch (e: NumberFormatException) {
            Log.e(TAG, "Error retrieving bitrate configuration.", e)
        }
        return rational
    }

    companion object {
        /**
         * Enabled closed captions for Picture-in-Picture.
         */
        const val PIP_CC_ENABLED = "picture_in_picture_cc_enabled"

        /**
         * When enabled, the activity will enter into Picture-in-Picture when pressing home key.
         */
        const val PIP_ON_USER_LEAVE_ENABLED = "picture_in_picture_on_user_leave_enabled"

        /**
         * Represents the percentage for CC to be reduced.
         */
        const val PIP_CC_SCALE_FACTOR = "picture_in_picture_cc_scale_factor"

        /**
         * Sets the aspect ratio for Picture-in-Picture.
         */
        const val PIP_ASPECT_RATIO = "picture_in_picture_aspect_ratio"

        private val TAG: String = SettingsModel::class.java.simpleName
    }
}
