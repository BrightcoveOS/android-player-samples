package com.brightcove.player.samples.pictureinpicture;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Rational;

import com.brightcove.player.logging.Log;

public class SettingsModel {

    /**
     * Enabled closed captions for Picture-in-Picture.
     */
    public static final String PIP_CC_ENABLED = "picture_in_picture_cc_enabled";
    /**
     * When enabled, the activity will enter into Picture-in-Picture when pressing home key.
     */
    public static final String PIP_ON_USER_LEAVE_ENABLED = "picture_in_picture_on_user_leave_enabled";
    /**
     * Represents the percentage for CC to be reduced.
     */
    public static final String PIP_CC_SCALE_FACTOR = "picture_in_picture_cc_scale_factor";
    /**
     * Sets the aspect ratio for Picture-in-Picture.
     */
    public static final String PIP_ASPECT_RATIO = "picture_in_picture_aspect_ratio";

    private static final String TAG = SettingsModel.class.getSimpleName();

    private final SharedPreferences preferences;

    public SettingsModel(@NonNull Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isPictureInPictureClosedCaptionsEnabled() {
        boolean defaultValue = false;
        return preferences.getBoolean(PIP_CC_ENABLED, defaultValue);
    }

    public boolean isPictureInPictureOnUserLeaveEnabled() {
        return preferences.getBoolean(PIP_ON_USER_LEAVE_ENABLED, false);
    }

    public float getPictureInPictureCCScaleFactor() {
        float scaleFactor = 0.5f;
        String scaleFactorString = preferences.getString(PIP_CC_SCALE_FACTOR, String.valueOf(scaleFactor));
        try {
            scaleFactor = Float.parseFloat(scaleFactorString);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error retrieving bitrate configuration.", e);
        }

        return scaleFactor;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Rational getPictureInPictureAspectRatio() {
        Rational rational = null;
        int numerator;
        int denominator;
        String rationalString = preferences.getString(PIP_ASPECT_RATIO, null);
        try {
            if (rationalString != null && rationalString.contains(":")) {
                String numbers[] = rationalString.split(":");
                numerator = Integer.parseInt(numbers[0]);
                denominator = Integer.parseInt(numbers[1]);
                Log.w("PIP", String.format("RATIONAL N=%s, D=%s", numerator, denominator));
                rational = new Rational(numerator, denominator);
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error retrieving bitrate configuration.", e);
        }

        return rational;
    }
}
