package com.brightcove.player.samples.exoplayer.basic;

import android.os.Bundle;
import android.util.Log;

import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.ExoPlayerVideoView;

/**
 * This app illustrates how to use the ExoPlayer with the Brightcove
 * Native Player SDK for Android.
 *
 * @author Billy Hnath (bhnath@brightcove.com)
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

//        // Add a test video to the BrightcoveVideoView.
//        Catalog catalog = new Catalog("ErQk9zUeDVLIp8Dc7aiHKq8hDMgkv5BFU7WGshTc-hpziB3BuYh28A..");
//        catalog.findPlaylistByReferenceID("stitch", new PlaylistListener() {
//            public void onPlaylist(Playlist playlist) {
//                brightcoveVideoView.addAll(playlist.getVideos());
//            }
//
//            public void onError(String error) {
//                Log.e(TAG, error);
//            }
//        });
//        brightcoveVideoView.setVideoPath("https://ia600408.us.archive.org/26/items/BigBuckBunny_328/BigBuckBunny_512kb.mp4");
//        Video video = Video.createVideo("http://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?"
//                + "as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,as&ip=0.0.0.0&"
//                + "ipbits=0&expire=19000000000&signature=255F6B3C07C753C88708C07EA31B7A1A10703C8D."
//                + "2D6A28B21F921D0B245CDCF36F7EB54A2B5ABFC2&key=ik0");
//        video.getProperties().put(Video.Fields.CONTENT_ID, "bf5bb2419360daf1");

//        Video video = Video.createVideo("http://vid2.cf.dmcdn.net/sec(e83834383dc2d0cbfa8b28a7bf9498fd)/video/999/334/122433999_mp4_h264_aac_ld.m3u8");
//        video.getProperties().put(Video.Fields.PUBLISHER_ID, "h2dk2d23232");
//        brightcoveVideoView.add(video);

//        Video video = Video.createVideo("https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8");
        Video video = Video.createVideo("https://s3.amazonaws.com/as-zencoder/hls-timed-metadata/test.m3u8");
        video.getProperties().put(Video.Fields.PUBLISHER_ID, "507017973001");
        brightcoveVideoView.add(video);

        // Log whether or not instance state in non-null.
        if (savedInstanceState != null) {
            Log.v(TAG, "Restoring saved position");
        } else {
            Log.v(TAG, "No saved state");
        }
    }
}