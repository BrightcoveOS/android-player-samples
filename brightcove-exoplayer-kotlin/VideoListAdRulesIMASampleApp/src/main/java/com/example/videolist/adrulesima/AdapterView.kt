package com.example.videolist.adrulesima

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brightcove.ima.GoogleIMAComponent
import com.brightcove.ima.GoogleIMAEventType
import com.brightcove.player.event.Event
import com.brightcove.player.event.EventEmitter
import com.brightcove.player.event.EventType
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcoveExoPlayerVideoView
import com.brightcove.player.view.BrightcoveVideoView
import com.google.ads.interactivemedia.v3.api.AdsRequest
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory

class AdapterView : RecyclerView.Adapter<AdapterView.ViewHolder>() {
    private val videoList: MutableList<Video> = ArrayList()
    private val adRulesURL =
        "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_ad_samples&sz=640x480&cust_params=sample_ct%3Dlinear&ciu_szs=300x250%2C728x90&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator="

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Get Video information
        val video = videoList[position]
        holder.videoTitleText.text = video.getStringProperty(Video.Fields.NAME)
        val videoView = holder.videoView
        videoView.clear()
        videoView.add(video)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        setupGoogleIMA(holder.videoView.eventEmitter, holder.videoView)
        holder.videoView.start()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.videoView.stopPlayback()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        //We need to stop the player to avoid a potential memory leak.
        for (i in 0 until recyclerView.childCount) {
            val holder = recyclerView.findViewHolderForAdapterPosition(i) as ViewHolder?
            if (holder?.videoView != null) {
                holder.videoView.stopPlayback()
            }
        }
    }

    fun setVideoList(videoList: List<Video>) {
        this.videoList.clear()
        this.videoList.addAll(videoList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val context: Context = itemView.context
        val videoTitleText: TextView =
            itemView.findViewById<View>(R.id.video_title_text) as TextView
        private val videoFrame: FrameLayout =
            itemView.findViewById<View>(R.id.video_frame) as FrameLayout
        val videoView: BrightcoveVideoView = BrightcoveExoPlayerVideoView(context)

        init {
            videoFrame.addView(videoView)
            videoView.finishInitialization()

            val eventEmitter = videoView.eventEmitter
            eventEmitter.on(EventType.ENTER_FULL_SCREEN) {
                //You can set listeners on each Video View
            }
        }
    }

    /**
     * Setup the Brightcove IMA Plugin.
     */
    private fun setupGoogleIMA(
        eventEmitter: EventEmitter,
        brightcoveVideoView: BrightcoveVideoView
    ) {
        val TAG = "setupGoogleIMA"
        // Establish the Google IMA SDK factory instance.
        val sdkFactory = ImaSdkFactory.getInstance()

        // Enable logging up ad start.
        eventEmitter.on(EventType.AD_STARTED) { event: Event ->
            Log.v(TAG, event.type)
        }

        // Enable logging any failed attempts to play an ad.
        eventEmitter.on(GoogleIMAEventType.DID_FAIL_TO_PLAY_AD) { event: Event ->
            Log.v(TAG, event.type)
        }

        // Enable Logging upon ad completion.
        eventEmitter.on(EventType.AD_COMPLETED) { event: Event ->
            Log.v(TAG, event.type)
        }

        // Set up a listener for initializing AdsRequests. The Google
        // IMA plugin emits an ad request event as a result of
        // initializeAdsRequests() being called.
        eventEmitter.on(GoogleIMAEventType.ADS_REQUEST_FOR_VIDEO) { event: Event ->
            // Build an ads request object and point it to the ad
            // display container created above.
            val adsRequest = sdkFactory.createAdsRequest()
            adsRequest.adTagUrl = adRulesURL

            val adsRequests = ArrayList<AdsRequest>(1)
            adsRequests.add(adsRequest)

            // Respond to the event with the new ad requests.
            event.properties[GoogleIMAComponent.ADS_REQUESTS] = adsRequests
            eventEmitter.respond(event)
        }

        // Create the Brightcove IMA Plugin and pass in the event
        // emitter so that the plugin can integrate with the SDK.
        val googleIMAComponent = GoogleIMAComponent.Builder(brightcoveVideoView, eventEmitter)
            .setUseAdRules(true)
            .build()
    }
}