package com.brightcove.player.samples.exoplayer.id3;

import android.os.Bundle;
import android.util.Log;
import com.brightcove.player.display.ExoPlayerVideoDisplayComponent;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventType;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.id3.Id3Frame;

import java.util.List;

/**
 * This app illustrates how to use ID3 tags with the Brightcove
 * ExoPlayer player for Android.
 *
 * @author Paul Matthew Reilly
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        brightcoveVideoView.getEventEmitter().on(EventType.DID_SET_SOURCE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                ExoPlayerVideoDisplayComponent exoPlayerVideoDisplayComponent =
                    (ExoPlayerVideoDisplayComponent) brightcoveVideoView.getVideoDisplay();
                exoPlayerVideoDisplayComponent.setMetadataListener(new ExoPlayerVideoDisplayComponent.MetadataListener() {
                    @Override
                    public void onMetadata(Metadata metadata) {
                        for(int i = 0; i < metadata.length(); i++) {
                            Metadata.Entry entry = metadata.get(i);
                            if (entry instanceof Id3Frame) {
                                Id3Frame id3Frame = (Id3Frame) entry;
                                Log.v(TAG, "id3 Frame id: " + id3Frame.id);
                            }
                        }
                    }
                });
            }
        });

        Video video = Video.createVideo("https://s3.amazonaws.com/as-zencoder/hls-timed-metadata/test.m3u8", DeliveryType.HLS);
        video.getProperties().put(Video.Fields.PUBLISHER_ID, "5420904993001");
        brightcoveVideoView.add(video);
        brightcoveVideoView.start();

        // Log whether or not instance state in non-null.
        if (savedInstanceState != null) {
            Log.v(TAG, "Restoring saved position");
        } else {
            Log.v(TAG, "No saved state");
        }
    }
}