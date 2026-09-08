package com.brightcove.player.samples.hdcpfallback.kotlin

import android.content.Context
import android.hardware.display.DisplayManager
import android.media.MediaDrm
import android.media.UnsupportedSchemeException
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.brightcove.player.Constants
import com.brightcove.player.display.ExoPlayerVideoDisplayComponent
import com.brightcove.player.event.Event
import com.brightcove.player.event.EventType
import com.brightcove.player.model.Source
import com.brightcove.player.view.BaseVideoView
import java.util.Locale
import kotlin.math.min

/**
 * Restricts rendition selection to the SD tier while the video output of the device does not meet
 * the HDCP level that the DRM licence policy of the account requires for HD.
 *
 * A video with HDCP fallback carries a separate DRM key per rendition tier. The SD key plays on any
 * output; the HD and UHD keys only play over an HDCP-protected one. The Widevine CDM enforces that
 * on the device, and it enforces it by refusing the key at output time. Playback then fails with a
 * decrypt error that the player cannot recover from. An application therefore has to keep an
 * unprotected output away from the HD renditions itself.
 *
 * Attach the guard once, after the video view exists, and call [release] from `onDestroy()`.
 *
 * @param minimumHdcpLevel the lowest [MediaDrm] HDCP level that unlocks the HD renditions. HDCP v1
 * is the platform default for HD content; raise it to [MediaDrm.HDCP_V2_2] when the licence policy
 * of the account asks for HDCP 2.2, as UHD content usually does.
 */
@OptIn(markerClass = [UnstableApi::class])
class HdcpFallback @JvmOverloads constructor(
    private val videoView: BaseVideoView,
    private val minimumHdcpLevel: Int = MediaDrm.HDCP_V1
) {

    private val videoDisplay = videoView.videoDisplay as ExoPlayerVideoDisplayComponent
    private val eventEmitter = videoView.eventEmitter
    private val displayManager =
        videoView.context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager?

    private var isRestrictedToSd = false
    private var unrestrictedMaxWidth = Int.MAX_VALUE
    private var unrestrictedMaxHeight = Int.MAX_VALUE
    private var unrestrictedExceedConstraints = true

    private val setSourceListenerToken = eventEmitter.on(EventType.SET_SOURCE) { event ->
        enableMultipleDrmSessions(event)
        applyHdcpPolicy()
    }

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = applyHdcpPolicy()
        override fun onDisplayRemoved(displayId: Int) = applyHdcpPolicy()
        override fun onDisplayChanged(displayId: Int) = applyHdcpPolicy()
    }

    init {
        displayManager?.registerDisplayListener(displayListener, Handler(Looper.getMainLooper()))
        applyHdcpPolicy()
    }

    fun release() {
        displayManager?.unregisterDisplayListener(displayListener)
        eventEmitter.off(EventType.SET_SOURCE, setSourceListenerToken)
        if (isRestrictedToSd) {
            videoDisplay.trackSelector?.let { liftSdRestriction(it) }
        }
    }

    /**
     * Each rendition tier has a key of its own, so moving from an SD rendition to an HD one needs a
     * second concurrent DRM session. Without this property the player keeps the session it opened
     * for the SD key and has no key for the HD renditions.
     */
    private fun enableMultipleDrmSessions(event: Event) {
        val source = event.properties[Event.SOURCE] as? Source ?: return
        if (source.hasKeySystem(Source.Fields.WIDEVINE_KEY_SYSTEM)) {
            source.properties[Source.Fields.MULTI_SESSION] = "true"
        }
    }

    private fun applyHdcpPolicy() {
        val restrictToSd = !hasProtectedOutput()
        if (restrictToSd == isRestrictedToSd) {
            return
        }
        val trackSelector = obtainTrackSelector()
        if (restrictToSd) {
            applySdRestriction(trackSelector)
        } else {
            liftSdRestriction(trackSelector)
        }
        isRestrictedToSd = restrictToSd
    }

    private fun applySdRestriction(trackSelector: DefaultTrackSelector) {
        val parameters = trackSelector.parameters
        unrestrictedMaxWidth = parameters.maxVideoWidth
        unrestrictedMaxHeight = parameters.maxVideoHeight
        unrestrictedExceedConstraints = parameters.exceedVideoConstraintsIfNecessary
        trackSelector.setParameters(
            trackSelector.buildUponParameters()
                .setMaxVideoSize(
                    min(unrestrictedMaxWidth, SD_MAX_WIDTH),
                    min(unrestrictedMaxHeight, SD_MAX_HEIGHT)
                )
                // The size constraint alone is a preference: the player still selects an HD
                // rendition when no rendition fits the constraint. Output protection has to fail
                // closed, so turn the preference into a limit while the restriction is in force.
                .setExceedVideoConstraintsIfNecessary(false)
        )
        Log.i(TAG, "The connected output is not HDCP protected: restricting selection to SD")
    }

    private fun liftSdRestriction(trackSelector: DefaultTrackSelector) {
        trackSelector.setParameters(
            trackSelector.buildUponParameters()
                .setMaxVideoSize(unrestrictedMaxWidth, unrestrictedMaxHeight)
                .setExceedVideoConstraintsIfNecessary(unrestrictedExceedConstraints)
        )
        Log.i(TAG, "The connected output is HDCP protected: allowing the whole rendition ladder")
    }

    /** Installs a selector when the SDK has not created its player yet, so the cap comes first. */
    private fun obtainTrackSelector(): DefaultTrackSelector =
        videoDisplay.trackSelector ?: DefaultTrackSelector(
            videoView.context,
            AdaptiveTrackSelection.Factory()
        ).also { videoDisplay.setTrackSelector(it) }

    private fun hasProtectedOutput(): Boolean {
        var mediaDrm: MediaDrm? = null
        try {
            mediaDrm = MediaDrm(Constants.WIDEVINE_UUID)
            val connectedLevel = readConnectedHdcpLevel(mediaDrm)
            Log.i(
                TAG,
                "Connected HDCP level: $connectedLevel, minimum level for HD: $minimumHdcpLevel"
            )
            // The MediaDrm levels rise with protection, and HDCP_NO_DIGITAL_OUTPUT - a device with
            // no digital output at all, such as a phone with nothing plugged in - is the highest of
            // them, because such an output is implicitly secure. HDCP_LEVEL_UNKNOWN is the lowest,
            // so an unreadable level restricts playback to SD.
            return connectedLevel >= minimumHdcpLevel
        } catch (exception: UnsupportedSchemeException) {
            Log.w(TAG, "This device does not support Widevine: restricting selection to SD")
            return false
        } finally {
            closeMediaDrm(mediaDrm)
        }
    }

    private fun readConnectedHdcpLevel(mediaDrm: MediaDrm): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                return mediaDrm.connectedHdcpLevel
            } catch (exception: RuntimeException) {
                Log.w(
                    TAG,
                    "getConnectedHdcpLevel failed, reading the hdcpLevel property instead",
                    exception
                )
            }
        }
        return parseHdcpLevel(getPropertyString(mediaDrm, "hdcpLevel"))
    }

    private fun getPropertyString(mediaDrm: MediaDrm, name: String): String? = try {
        mediaDrm.getPropertyString(name)
    } catch (exception: RuntimeException) {
        // Some Widevine builds throw for a property they do not implement.
        Log.w(TAG, "Cannot read the $name property", exception)
        null
    }

    @Suppress("DEPRECATION")
    private fun closeMediaDrm(mediaDrm: MediaDrm?) {
        if (mediaDrm == null) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mediaDrm.close()
        } else {
            mediaDrm.release()
        }
    }

    companion object {
        private const val TAG = "HdcpFallback"

        /** The largest rendition the SD tier holds. Mirrors the rendition thresholds of the account. */
        private const val SD_MAX_WIDTH = 1279
        private const val SD_MAX_HEIGHT = 719

        /** Maps the vendor "hdcpLevel" property of API 27 and below onto a [MediaDrm] level. */
        private fun parseHdcpLevel(value: String?): Int {
            if (value.isNullOrEmpty()) {
                return MediaDrm.HDCP_LEVEL_UNKNOWN
            }
            val level = value.lowercase(Locale.US).replace(" ", "")
            return when {
                level.contains("nodigitaloutput") -> MediaDrm.HDCP_NO_DIGITAL_OUTPUT
                level.contains("unprotected") -> MediaDrm.HDCP_NONE
                level.startsWith("hdcp-2.3") -> MediaDrm.HDCP_V2_3
                level.startsWith("hdcp-2.2") -> MediaDrm.HDCP_V2_2
                level.startsWith("hdcp-2.1") -> MediaDrm.HDCP_V2_1
                level.startsWith("hdcp-2") -> MediaDrm.HDCP_V2
                level.startsWith("hdcp-1") -> MediaDrm.HDCP_V1
                else -> MediaDrm.HDCP_LEVEL_UNKNOWN
            }
        }
    }
}
