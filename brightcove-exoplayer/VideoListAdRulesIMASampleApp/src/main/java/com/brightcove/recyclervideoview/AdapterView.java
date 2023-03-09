package com.brightcove.recyclervideoview;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.brightcove.ima.GoogleIMAComponent;
import com.brightcove.ima.GoogleIMAEventType;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcoveVideoView;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;

import java.util.ArrayList;
import java.util.List;

public class AdapterView extends RecyclerView.Adapter<AdapterView.ViewHolder> {

    private final List<Video> videoList = new ArrayList<>();
    private String adRulesURL = "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_ad_samples&sz=640x480&cust_params=sample_ct%3Dlinear&ciu_szs=300x250%2C728x90&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator=";
//    private GoogleIMAComponent googleIMAComponent;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Get Video information
        Video video = videoList.get(position);
        if (video != null) {
            holder.videoTitleText.setText(video.getStringProperty(Video.Fields.NAME));
            BrightcoveVideoView videoView = holder.videoView;
            videoView.clear();
            videoView.add(video);
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        setupGoogleIMA(holder.videoView.getEventEmitter(), holder.videoView);
        holder.videoView.start();
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.videoView.stopPlayback();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        int childCount = recyclerView.getChildCount();
        //We need to stop the player to avoid a potential memory leak.
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null && holder.videoView != null) {
                holder.videoView.stopPlayback();
            }
        }
    }

    public void setVideoList(@Nullable List<Video> videoList) {
        this.videoList.clear();
        this.videoList.addAll(videoList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final Context context;
        public final TextView videoTitleText;
        public final FrameLayout videoFrame;
        public final BrightcoveVideoView videoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            videoFrame = (FrameLayout) itemView.findViewById(R.id.video_frame);
            videoTitleText = (TextView) itemView.findViewById(R.id.video_title_text);
            videoView = new BrightcoveExoPlayerVideoView(context);
            videoFrame.addView(videoView);
            videoView.finishInitialization();

            EventEmitter eventEmitter = videoView.getEventEmitter();
            eventEmitter.on(EventType.ENTER_FULL_SCREEN, new EventListener() {
                @Override
                public void processEvent(Event event) {
                    //You can set listeners on each Video View
                }
            });
        }
    }

    /**
     * Setup the Brightcove IMA Plugin.
     */
    private void setupGoogleIMA(EventEmitter eventEmitter, BrightcoveVideoView brightcoveVideoView) {
        String TAG = "setupGoogleIMA";
        // Establish the Google IMA SDK factory instance.
        final ImaSdkFactory sdkFactory = ImaSdkFactory.getInstance();

        // Enable logging up ad start.
        eventEmitter.on(EventType.AD_STARTED, event -> Log.v(TAG, event.getType()));

        // Enable logging any failed attempts to play an ad.
        eventEmitter.on(GoogleIMAEventType.DID_FAIL_TO_PLAY_AD, event -> Log.v(TAG, event.getType()));

        // Enable Logging upon ad completion.
        eventEmitter.on(EventType.AD_COMPLETED, event -> Log.v(TAG, event.getType()));

        // Set up a listener for initializing AdsRequests. The Google
        // IMA plugin emits an ad request event as a result of
        // initializeAdsRequests() being called.
        eventEmitter.on(GoogleIMAEventType.ADS_REQUEST_FOR_VIDEO, event -> {
            // Build an ads request object and point it to the ad
            // display container created above.
            AdsRequest adsRequest = sdkFactory.createAdsRequest();
            adsRequest.setAdTagUrl(adRulesURL);

            ArrayList<AdsRequest> adsRequests = new ArrayList<>(1);
            adsRequests.add(adsRequest);

            // Respond to the event with the new ad requests.
            event.properties.put(GoogleIMAComponent.ADS_REQUESTS, adsRequests);
            eventEmitter.respond(event);
        });

        // Create the Brightcove IMA Plugin and pass in the event
        // emitter so that the plugin can integrate with the SDK.
        GoogleIMAComponent googleIMAComponent = new GoogleIMAComponent.Builder(brightcoveVideoView, eventEmitter)
                .setUseAdRules(true)
                .build();
    }
}
