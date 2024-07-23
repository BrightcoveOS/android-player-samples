package com.brightcove.player.samples.ssai.pal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.brightcove.player.Sdk;
import com.brightcove.player.appcompat.BrightcovePlayerActivity;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.Video;
import com.brightcove.player.network.HttpRequestConfig;
import com.brightcove.player.samples.ssai.pal.R;
import com.brightcove.ssai.SSAIComponent;
import com.brightcove.ssai.event.SSAIEventType;
import com.brightcove.ssai.omid.AdEventType;
import com.brightcove.ssai.omid.OpenMeasurementTracker;
import com.google.ads.interactivemedia.pal.ConsentSettings;
import com.google.ads.interactivemedia.pal.NonceLoader;
import com.google.ads.interactivemedia.pal.NonceManager;
import com.google.ads.interactivemedia.pal.NonceRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.iab.omid.library.brightcove.adsession.FriendlyObstructionPurpose;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends BrightcovePlayerActivity {

    private static final String TAG = "MainActivity";
    private static final String AD_CONFIG_ID_QUERY_PARAM_VALUE = "ba5e4879-77f0-424b-8c98-706ae5ad7eec";
    private static final String PARTNER_NAME = "dummyVendor";
    private static final String PARTNER_VERSION = Sdk.getVersionName();

    private SSAIComponent ssaiPlugin;
    private OpenMeasurementTracker omTracker;

    private NonceLoader nonceLoader;
    private NonceManager nonceManager;
    private ConsentSettings consentSettings;

    private Catalog catalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign brightcoveVideoView before
        // entering the superclass.  This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.ssai_activity_main);
        baseVideoView = findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        // The default value for allowStorage() is false, but can be
        // changed once the appropriate consent has been gathered. The
        // getConsentToStorage() method is a placeholder for the publisher's own
        // method of obtaining user consent, either by integrating with a CMP or
        // based on other methods the publisher chooses to handle storage consent.
        //boolean isConsentToStorage = getConsentToStorage();
        consentSettings = ConsentSettings.builder()
                .allowStorage(false)
                .build();
        // It is important to instantiate the NonceLoader as early as possible to
        // allow it to initialize and preload data for a faster experience when
        // loading the NonceManager. A new NonceLoader will need to be instantiated
        //if the ConsentSettings change for the user.
        nonceLoader = new NonceLoader(this, consentSettings);

        //PAL
        generateNonceForAdRequest();

        final EventEmitter eventEmitter = baseVideoView.getEventEmitter();

        catalog = new Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
                .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
                .setPolicy(getString(R.string.sdk_demo_policy_key))
                .build();

        // Setup the error event handler for the SSAI plugin.
        registerErrorEventHandler();

        setupOpenMeasurement();
        ssaiPlugin = new SSAIComponent(this, baseVideoView);
        View view = findViewById(R.id.ad_frame);
        if (view instanceof ViewGroup) {
            // Set the companion ad container,
            ssaiPlugin.addCompanionContainer((ViewGroup) view);
        }

        baseVideoView.getEventEmitter().on(EventType.PLAY, event -> sendPlaybackStart());

        baseVideoView.getEventEmitter().on(EventType.COMPLETED, event -> sendPlaybackEnd());

        baseVideoView.getEventEmitter().on(SSAIEventType.AD_CLICKED, event -> sendAdClick());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (omTracker != null && isFinishing()) {
            omTracker.stop();
        }
        if (nonceLoader != null) {
            nonceLoader.release();
        }
    }


    public void generateNonceForAdRequest() {
        Set supportedApiFrameWorksSet = new HashSet();
        // The values 2, 7, and 9 correspond to player support for VPAID 2.0,
        // OMID 1.0, and SIMID 1.1.
        supportedApiFrameWorksSet.add(2);
        supportedApiFrameWorksSet.add(7);
        supportedApiFrameWorksSet.add(9);

        NonceRequest nonceRequest = NonceRequest.builder()
                .descriptionURL("https://example.com/content1")
                .iconsSupported(true)
                .omidPartnerVersion("6.2.1")
                .omidPartnerName("Example Publisher")
                .playerType("ExamplePlayerType")
                .playerVersion("1.0.0")
                .ppid("testPpid")
                .sessionId("Sample SID")
                .supportedApiFrameworks(supportedApiFrameWorksSet)
                .videoPlayerHeight(480)
                .videoPlayerWidth(640)
                .willAdAutoPlay(true)
                .willAdPlayMuted(true)
                .build();
        NonceCallbackImpl callback = new NonceCallbackImpl();
        nonceLoader
                .loadNonceManager(nonceRequest)
                .addOnSuccessListener(callback)
                .addOnFailureListener(callback);
    }

    private class NonceCallbackImpl implements OnSuccessListener<NonceManager>, OnFailureListener {
        @Override
        public void onSuccess(NonceManager manager) {
            nonceManager = manager;
            String nonceString = manager.getNonce();
            ssaiPlugin.setNonce(nonceString);
            Log.d("PALSample", "Generated nonce: " + nonceString);
            // Set the HttpRequestConfig with the Ad Config Id configured in
            // your https://studio.brightcove.com account.
            HttpRequestConfig httpRequestConfig = new HttpRequestConfig.Builder()
                    .addQueryParameter(HttpRequestConfig.KEY_AD_CONFIG_ID, AD_CONFIG_ID_QUERY_PARAM_VALUE)
                    .build();

            catalog.findVideoByID(getString(R.string.sdk_demo_video_id), httpRequestConfig, new VideoListener() {
                @Override
                public void onVideo(Video video) {
                    // The Video Sources will have a VMAP url which will be processed by the SSAI plugin,
                    // If there is not a VMAP url, or if there are any requesting or parsing error,
                    // an EventType.ERROR event will be emitted.
                    ssaiPlugin.processVideo(video);
                }
            });
        }

        @Override
        public void onFailure(Exception error) {
            Log.e("PALSample", "Nonce generation failed: " + error.getMessage());
        }
    }

    public void sendAdClick() {
        if (nonceManager != null) {
            nonceManager.sendAdClick();
            Log.d(TAG, "PAL sendAdClick() called");
        }
    }

    public void sendPlaybackStart() {
        if (nonceManager != null) {
            nonceManager.sendPlaybackStart();
            Log.d(TAG, "PAL sendPlaybackStart() called");
        }
    }

    public void sendPlaybackEnd() {
        if (nonceManager != null) {
            nonceManager.sendPlaybackEnd();
            Log.d(TAG, "PAL sendPlaybackEnd() called");
        }
    }

    private void setupOpenMeasurement() {
        SwitchCompat toggleButton = findViewById(R.id.om_toggle);
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                omTracker.start();
            } else {
                omTracker.stop();
            }
        });
        // Initialize the OpenMeasurementTracker
        omTracker = new OpenMeasurementTracker.Factory(
                PARTNER_NAME, PARTNER_VERSION, baseVideoView
        ).create();
        // NOTE: The ad used in the sample does not have an `AdVerification` element and will not
        //       send tracking events.  You may verify OpenMeasurement via the following listener:
        omTracker.addListener(new OpenMeasurementTracker.Listener() {
            @Override
            public void onEvent(AdEventType adEventType) {
                Log.d(TAG, "onEvent() called with: adEventType = [" + adEventType + "]");
            }

            @Override
            public void onStartTracking() {
                Log.d(TAG, "onStartTracking() called");
            }

            @Override
            public void onStoppedTracking() {
                Log.d(TAG, "onStoppedTracking() called");
            }
        });
        // Example to register a view that should be considered as a friendly obstruction
        View adFrame = findViewById(R.id.ad_frame);
        omTracker.addFriendlyObstruction(adFrame, FriendlyObstructionPurpose.OTHER, "Ad frame");
        // Start the tracker, if enabled.
        if (toggleButton.isChecked()) {
            omTracker.start();
        }
    }

    private void registerErrorEventHandler() {
        // Handle the case where the ad data URL has not been supplied to the plugin.
        EventEmitter eventEmitter = baseVideoView.getEventEmitter();
        eventEmitter.on(EventType.ERROR, event -> Log.e(TAG, event.getType()));
    }
}
