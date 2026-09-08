package com.brightcove.player.samples.hdcpfallback.java;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.media.MediaDrm;
import android.media.UnsupportedSchemeException;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.annotation.RequiresApi;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;

import com.brightcove.player.Constants;
import com.brightcove.player.display.ExoPlayerVideoDisplayComponent;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.Source;
import com.brightcove.player.view.BaseVideoView;

import java.util.Locale;

/**
 * Restricts rendition selection to the SD tier while the video output of the device does not meet
 * the HDCP level that the DRM licence policy of the account requires for HD.
 *
 * <p>A video with HDCP fallback carries a separate DRM key per rendition tier. The SD key plays on
 * any output; the HD and UHD keys only play over an HDCP-protected one. The Widevine CDM enforces
 * that on the device, and it enforces it by refusing the key at output time. Playback then fails
 * with a decrypt error that the player cannot recover from. An application therefore has to keep
 * an unprotected output away from the HD renditions itself.</p>
 *
 * <p>Attach the guard once, after the video view exists:</p>
 *
 * <pre>{@code
 * hdcpFallback = HdcpFallback.attach(brightcoveVideoView);
 * }</pre>
 *
 * <p>and call {@link #release()} from {@code onDestroy()}.</p>
 */
@OptIn(markerClass = UnstableApi.class)
public final class HdcpFallback {

    private static final String TAG = HdcpFallback.class.getSimpleName();

    /** The largest rendition the SD tier holds. Mirrors the rendition thresholds of the account. */
    private static final int SD_MAX_WIDTH = 1279;
    private static final int SD_MAX_HEIGHT = 719;

    /**
     * The lowest HDCP level that unlocks the HD renditions. HDCP v1 is the platform default for HD
     * content; raise it to {@link MediaDrm#HDCP_V2_2} when the licence policy of the account asks
     * for HDCP 2.2, as UHD content usually does.
     */
    private final int minimumHdcpLevel;

    private final BaseVideoView videoView;
    private final ExoPlayerVideoDisplayComponent videoDisplay;
    private final EventEmitter eventEmitter;
    @Nullable
    private final DisplayManager displayManager;

    private boolean isRestrictedToSd;
    private int unrestrictedMaxWidth = Integer.MAX_VALUE;
    private int unrestrictedMaxHeight = Integer.MAX_VALUE;
    private boolean unrestrictedExceedConstraints = true;

    private final int setSourceListenerToken;

    private final DisplayManager.DisplayListener displayListener = new DisplayManager.DisplayListener() {
        @Override
        public void onDisplayAdded(int displayId) {
            applyHdcpPolicy();
        }

        @Override
        public void onDisplayRemoved(int displayId) {
            applyHdcpPolicy();
        }

        @Override
        public void onDisplayChanged(int displayId) {
            applyHdcpPolicy();
        }
    };

    /** Attaches the guard with HDCP v1 as the level that unlocks HD. */
    @NonNull
    public static HdcpFallback attach(@NonNull BaseVideoView videoView) {
        return new HdcpFallback(videoView, MediaDrm.HDCP_V1);
    }

    /** Attaches the guard with the given minimum {@link MediaDrm} HDCP level for HD. */
    @NonNull
    public static HdcpFallback attach(@NonNull BaseVideoView videoView, int minimumHdcpLevel) {
        return new HdcpFallback(videoView, minimumHdcpLevel);
    }

    private HdcpFallback(@NonNull BaseVideoView videoView, int minimumHdcpLevel) {
        this.videoView = videoView;
        this.videoDisplay = (ExoPlayerVideoDisplayComponent) videoView.getVideoDisplay();
        this.eventEmitter = videoView.getEventEmitter();
        this.minimumHdcpLevel = minimumHdcpLevel;
        this.displayManager =
                (DisplayManager) videoView.getContext().getSystemService(Context.DISPLAY_SERVICE);

        setSourceListenerToken = eventEmitter.on(EventType.SET_SOURCE, event -> {
            enableMultipleDrmSessions(event);
            applyHdcpPolicy();
        });

        if (displayManager != null) {
            displayManager.registerDisplayListener(
                    displayListener, new Handler(Looper.getMainLooper()));
        }

        applyHdcpPolicy();
    }

    public void release() {
        if (displayManager != null) {
            displayManager.unregisterDisplayListener(displayListener);
        }
        eventEmitter.off(EventType.SET_SOURCE, setSourceListenerToken);
        if (isRestrictedToSd) {
            liftSdRestriction(videoDisplay.getTrackSelector());
        }
    }

    /**
     * Each rendition tier has a key of its own, so moving from an SD rendition to an HD one needs a
     * second concurrent DRM session. Without this property the player keeps the session it opened
     * for the SD key and has no key for the HD renditions.
     */
    private void enableMultipleDrmSessions(@NonNull Event event) {
        Object source = event.getProperties().get(Event.SOURCE);
        if (source instanceof Source
                && ((Source) source).hasKeySystem(Source.Fields.WIDEVINE_KEY_SYSTEM)) {
            ((Source) source).getProperties().put(Source.Fields.MULTI_SESSION, "true");
        }
    }

    private void applyHdcpPolicy() {
        boolean restrictToSd = !hasProtectedOutput();
        if (restrictToSd == isRestrictedToSd) {
            return;
        }
        DefaultTrackSelector trackSelector = obtainTrackSelector();
        if (restrictToSd) {
            applySdRestriction(trackSelector);
        } else {
            liftSdRestriction(trackSelector);
        }
        isRestrictedToSd = restrictToSd;
    }

    private void applySdRestriction(@NonNull DefaultTrackSelector trackSelector) {
        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
        unrestrictedMaxWidth = parameters.maxVideoWidth;
        unrestrictedMaxHeight = parameters.maxVideoHeight;
        unrestrictedExceedConstraints = parameters.exceedVideoConstraintsIfNecessary;
        trackSelector.setParameters(trackSelector.buildUponParameters()
                .setMaxVideoSize(
                        Math.min(unrestrictedMaxWidth, SD_MAX_WIDTH),
                        Math.min(unrestrictedMaxHeight, SD_MAX_HEIGHT))
                // The size constraint alone is a preference: the player still selects an HD
                // rendition when no rendition fits the constraint. Output protection has to fail
                // closed, so turn the preference into a limit while the restriction is in force.
                .setExceedVideoConstraintsIfNecessary(false)
                .build());
        Log.i(TAG, "The connected output is not HDCP protected: restricting selection to SD");
    }

    private void liftSdRestriction(@Nullable DefaultTrackSelector trackSelector) {
        if (trackSelector == null) {
            return;
        }
        trackSelector.setParameters(trackSelector.buildUponParameters()
                .setMaxVideoSize(unrestrictedMaxWidth, unrestrictedMaxHeight)
                .setExceedVideoConstraintsIfNecessary(unrestrictedExceedConstraints)
                .build());
        Log.i(TAG, "The connected output is HDCP protected: allowing the whole rendition ladder");
    }

    /** Installs a selector when the SDK has not created its player yet, so the cap comes first. */
    @NonNull
    private DefaultTrackSelector obtainTrackSelector() {
        DefaultTrackSelector trackSelector = videoDisplay.getTrackSelector();
        if (trackSelector == null) {
            trackSelector = new DefaultTrackSelector(
                    videoView.getContext(), new AdaptiveTrackSelection.Factory());
            videoDisplay.setTrackSelector(trackSelector);
        }
        return trackSelector;
    }

    private boolean hasProtectedOutput() {
        MediaDrm mediaDrm = null;
        try {
            mediaDrm = new MediaDrm(Constants.WIDEVINE_UUID);
            int connectedLevel = readConnectedHdcpLevel(mediaDrm);
            Log.i(TAG, "Connected HDCP level: " + connectedLevel
                    + ", minimum level for HD: " + minimumHdcpLevel);
            // The MediaDrm levels rise with protection, and HDCP_NO_DIGITAL_OUTPUT - a device with
            // no digital output at all, such as a phone with nothing plugged in - is the highest of
            // them, because such an output is implicitly secure. HDCP_LEVEL_UNKNOWN is the lowest,
            // so an unreadable level restricts playback to SD.
            return connectedLevel >= minimumHdcpLevel;
        } catch (UnsupportedSchemeException exception) {
            Log.w(TAG, "This device does not support Widevine: restricting selection to SD");
            return false;
        } finally {
            closeMediaDrm(mediaDrm);
        }
    }

    private static int readConnectedHdcpLevel(@NonNull MediaDrm mediaDrm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                return getConnectedHdcpLevel(mediaDrm);
            } catch (RuntimeException exception) {
                Log.w(TAG, "getConnectedHdcpLevel failed, reading the hdcpLevel property instead",
                        exception);
            }
        }
        return parseHdcpLevel(getPropertyString(mediaDrm, "hdcpLevel"));
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private static int getConnectedHdcpLevel(@NonNull MediaDrm mediaDrm) {
        return mediaDrm.getConnectedHdcpLevel();
    }

    @Nullable
    private static String getPropertyString(@NonNull MediaDrm mediaDrm, @NonNull String name) {
        try {
            return mediaDrm.getPropertyString(name);
        } catch (RuntimeException exception) {
            // Some Widevine builds throw for a property they do not implement.
            Log.w(TAG, "Cannot read the " + name + " property", exception);
            return null;
        }
    }

    /** Maps the vendor "hdcpLevel" property of API 27 and below onto a {@link MediaDrm} level. */
    private static int parseHdcpLevel(@Nullable String value) {
        if (TextUtils.isEmpty(value)) {
            return MediaDrm.HDCP_LEVEL_UNKNOWN;
        }
        String level = value.toLowerCase(Locale.US).replace(" ", "");
        if (level.contains("nodigitaloutput")) return MediaDrm.HDCP_NO_DIGITAL_OUTPUT;
        if (level.contains("unprotected")) return MediaDrm.HDCP_NONE;
        if (level.startsWith("hdcp-2.3")) return MediaDrm.HDCP_V2_3;
        if (level.startsWith("hdcp-2.2")) return MediaDrm.HDCP_V2_2;
        if (level.startsWith("hdcp-2.1")) return MediaDrm.HDCP_V2_1;
        if (level.startsWith("hdcp-2")) return MediaDrm.HDCP_V2;
        if (level.startsWith("hdcp-1")) return MediaDrm.HDCP_V1;
        return MediaDrm.HDCP_LEVEL_UNKNOWN;
    }

    @SuppressWarnings("deprecation")
    private static void closeMediaDrm(@Nullable MediaDrm mediaDrm) {
        if (mediaDrm == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mediaDrm.close();
        } else {
            mediaDrm.release();
        }
    }
}
