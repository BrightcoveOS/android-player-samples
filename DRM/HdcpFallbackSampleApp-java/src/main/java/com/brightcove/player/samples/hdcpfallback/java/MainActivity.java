package com.brightcove.player.samples.hdcpfallback.java;

import android.os.Bundle;
import android.util.Log;

import androidx.media3.common.Format;

import com.brightcove.player.display.ExoPlayerVideoDisplayComponent;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.CatalogError;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;

import java.util.List;

/**
 * This app illustrates HDCP fallback with the Brightcove Native Player SDK for Android.
 *
 * <p>The video comes from an account that assigns a DRM key per rendition tier: the SD key plays on
 * any output, the HD key only plays over an HDCP-protected one. {@link HdcpFallback} reads the HDCP
 * state of the connected output and restricts rendition selection to SD while that output is
 * unprotected, so both classes of device play the video: a protected output adapts up to HD, and an
 * unprotected one stays on SD instead of failing with a decrypt error.</p>
 */
public class MainActivity extends BrightcovePlayer {

    private static final String TAG = MainActivity.class.getSimpleName();

    private HdcpFallback hdcpFallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use its event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        hdcpFallback = HdcpFallback.attach(brightcoveVideoView);

        logSelectedRendition();

        Catalog catalog = new Catalog.Builder(brightcoveVideoView.getEventEmitter(), getString(R.string.sdk_demo_account))
                .setPolicy(getString(R.string.sdk_demo_policy))
                .build();

        catalog.findVideoByID(getString(R.string.sdk_demo_video_id), new VideoListener() {
            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
                brightcoveVideoView.start();
            }

            @Override
            public void onError(List<CatalogError> errors) {
                Log.e(TAG, errors.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        hdcpFallback.release();
        super.onDestroy();
    }

    /** Reports every rendition change, so that the effect of the HDCP policy is visible in logcat. */
    private void logSelectedRendition() {
        brightcoveVideoView.getEventEmitter().on(ExoPlayerVideoDisplayComponent.RENDITION_CHANGED, event -> {
            Format format = event.getProperty(
                    ExoPlayerVideoDisplayComponent.EXOPLAYER_FORMAT, Format.class);
            if (format != null && format.height > 0) {
                Log.i(TAG, "Playing rendition " + format.width + "x" + format.height
                        + " at " + format.bitrate + " bps");
            }
        });
    }
}
