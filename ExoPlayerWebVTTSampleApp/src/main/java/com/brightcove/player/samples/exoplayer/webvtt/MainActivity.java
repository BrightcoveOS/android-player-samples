package com.brightcove.player.samples.exoplayer.webvtt;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.media.MediaFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.VideoListener;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.ExoPlayerVideoView;

/**
 * This app illustrates how to use the ExoPlayer with the Brightcove
 * Native Player SDK for Android and Web-VTT Captions.
 *
 * @author Paul Matthew Reilly (original code, based on Github history)
 * @author Jim Whisenant (
 *          adapted this example from WebVTTSampleApp
 *          added test data
 *          added a WIP Task to retrieve captions instead of load them from the local filesystem)
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (ExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        // HLSe URL
        String hlseURLSecureSegments = "http://c.brightcove.com/services/mobile/streaming/index/master.m3u8?videoId=3520544323001&pubId=252362759&lineupId=&affiliateId=";


        String dashBCOVSampleURL = "http://brightcove.vo.llnwd.net/o2/unsecured/media/1232842446001/201408/3393/1232842446001_3721640754001_1687-ColoCribs-Aug8-a.mpd";
        String dashNonBCOVSampleURL = "http://bucket01.mscreentv.com.s3.amazonaws.com/prdash/slmulti-iistypetemplate/manifest3.mpd";
        String dashYoutubeSampleURL = "http://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?" +
                                        "as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,as&ip=0.0.0.0&" +
                                        "ipbits=0&expire=19000000000&signature=255F6B3C07C753C88708C07EA31B7A1A10703C8D." +
                                        "2D6A28B21F921D0B245CDCF36F7EB54A2B5ABFC2&key=ik0";

        // HLS Live, no DVR
        String bcovLiveHLSURL = "http://bcoveliveios-i.akamaihd.net/hls/live/215102/master_english/398/master.m3u8?playerId=255166148200&lineupId=&affiliateId=&pubId=2402232199001&videoId=2552000984001";

        // Akamai HD2 HLS, non-encrypted
        String hd2HLSURL = "http://bcqaus-vh.akamaihd.net/i/370335738/201402/370335738_32502,70843,70819,58359,70818,70824,70825,001_Punisher.mp4.csmil/master.m3u8?videoId=3250236582001";

        // Akamai HD2 HLS, encrypted - Returns a 403
        String hd2HLSTokenAuthURL = "http://bcqausauth-vh.akamaihd.net/i/370335738/201402/370335738_32512971,81,91,83,80,78,82,001_Punisher.mp4.csmil/master.m3u8?videoId=3251176046001&hdnea=st=1393280474~exp=1424816474~acl=/*~hmac=f0687ccc31cda7c074a9b9ed75f1928e0503780c69d22317e4eb761d0e800b89";

        // HLS served over https
        String bcovHttpsHLSURL = "https://secure.brightcove.com/services/mobile/streaming/index/master.m3u8?videoId=4073047675001";

        // Not currently supported
        String onceAdaptiveContinuousTimestampHLSURL = "http://once.unicornmedia.com/now/ctadaptive/m3u8/77128e82-3acd-4064-ab71-f6de437aba5d/da63d706-dae8-4110-9035-e7e79b753ed0/f1a96e7e-d5b8-4737-a527-45c0f79702d9/content.m3u8";
        String onceLongFormNoAdsURL = "http://onceux.unicornmedia.com/now/ads/vmap/od/auto/28c23297-a1ab-4eb5-a697-7d6942db4efb/d202862f-bb88-41c0-921e-27baf5ade003/7d5bdb24-2e0d-4b09-b2c9-7dbf01838f8f/content.once";

//        Video video = Video.createVideo(hd2HLSURL);
//        video.getProperties().put(Video.Fields.CONTENT_ID, "bf5bb2419360daf1");
//        brightcoveVideoView.add(video);

        // The examples below can all use the Brightcove APIs to retrieve sample/test content
        String hlsOnlyAPIToken = "UV3EUeje-jlI5sUpJAGsDZ2jki26BZl78pRKemVDxNTXAxyVOabPdA..";
        String mp4OnlyAPIToken = "ZUPNyrUqRdcAtjytsjcJplyUc9ed8b0cD_eWIe36jXqNWKzIcE6i8A..";
        String dashAPIToken = "rN4S88gZ7540qnsB2iPH33iIHzYLCA5i1JtSgEV_KMxnflUiZ2iFaQ..";

        // Brightcove Encrypted MPEG-Dash
        String dashReferenceId = "TC_build_44_a4d7fcf4-9f77-466b-ab30-2b6ace0e8bac";

        // HLS, single rendition
        String hlsOnlySingleRenditionReferenceId = "10sec-nocaps";

        // HLS, multiple renditions
        String hlsOnlyMultiRenditionReferenceId = "66sec-multi-rendition-with-audio-rendition";

        // HLSe, segments served over https
        String hlsHTTPSReferenceId = "kungfu-secure";

        // MP4, multiple renditions
        String mp4OnlyMultiRenditionReferenceId = "75sec-mp4-multi-rendition";

        // Add a test video to the BrightcoveVideoView.
        Catalog catalog = new Catalog(mp4OnlyAPIToken);
        catalog.findVideoByReferenceID(mp4OnlyMultiRenditionReferenceId, new VideoListener() {
            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
            }

            @Override
            public void onError(String s) {
                Log.e(TAG, "Could not load video: " + s);
            }
        });

        setUpVTTCaptions();

        // Log whether or not instance state in non-null.
        if (savedInstanceState != null) {
            Log.v(TAG, "Restoring saved position");
        } else {
            Log.v(TAG, "No saved state");
        }
    }

    private void setUpVTTCaptions() {

        new RetrieveCaptionsTask().execute("http://solutions.brightcove.com/jwhisenant/testpages/captions/vtt/bc_smart_en-76sec.vtt");

            MediaFormat mediaFormat = MediaFormat.createSubtitleFormat("text/vtt", "en");
            brightcoveVideoView.addSubtitleSource(new RetrieveCaptionsTask().captionsInputStream, mediaFormat);

    }
}

class RetrieveCaptionsTask extends AsyncTask<String, Void, InputStream> {

    private Exception exception;
    URL captionsUrl;
    InputStream captionsInputStream;

    protected InputStream doInBackground(String... urls) {
        try {
            URL captionsUrl = new URL(urls[0]);
            captionsInputStream = captionsUrl.openStream();
            return captionsUrl.openStream();
        } catch (Exception e) {
            this.exception = e;
            if (e instanceof MalformedURLException) {
                Log.v(this.getClass().getSimpleName(), "Could not retrieve captions.");
            } else if (e instanceof IOException) {
                Log.v(this.getClass().getSimpleName(), "Could not set up an InputStream from the retrieved captions.");
            } else if (e instanceof NullPointerException) {
                Log.v(this.getClass().getSimpleName(), "Captions stream URL was null");
            }
            return null;
        }
    }

    protected void onPostExecute(InputStream stream) {

    }
}