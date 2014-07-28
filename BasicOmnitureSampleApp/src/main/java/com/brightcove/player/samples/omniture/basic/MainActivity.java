package com.brightcove.player.samples.omniture.basic;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;

import com.brightcove.omniture.OmnitureComponent;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventLogger;
import com.brightcove.player.event.EventType;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.PlaylistListener;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;

/**
 * This app illustrates how to use the Omniture plugin with the Brightcove Player for Android.
 *
 * @author Billy Hnath
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private EventEmitter eventEmitter;
    private OmnitureComponent omnitureComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView
        // before entering the superclass. This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.omniture_activity_main );
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        eventEmitter = brightcoveVideoView.getEventEmitter();

        setupOmniture();

        // Add a test video to the BrightcoveVideoView.
        Catalog catalog = new Catalog("ErQk9zUeDVLIp8Dc7aiHKq8hDMgkv5BFU7WGshTc-hpziB3BuYh28A..");
        catalog.findPlaylistByReferenceID("stitch", new PlaylistListener() {
            public void onPlaylist(Playlist playlist) {
                brightcoveVideoView.addAll(playlist.getVideos());
            }

            public void onError(String error) {
                Log.e(TAG, error);
            }
        });
    }

    private void setupOmniture() {

        omnitureComponent = new OmnitureComponent(eventEmitter,
                this,
                "Android Sample App",
                "Android Sample Player");
//        ADMS_Measurement measurement = omnitureComponent.getMeasurement();
//        measurement.setDebugLogging(true);
//        measurement.setCharSet("UTF-8");
//        ADMS_MediaMeasurement mediaMeasurement = omnitureComponent.getMediaMeasurement();
//        mediaMeasurement.trackSeconds = 8;
//        mediaMeasurement.trackMilestones = "25,50,75";
//        mediaMeasurement.segmentByMilestones = true;
//        mediaMeasurement.trackVars = "eVar1,eVar2,eVar3,prop1,prop2,prop3,prop4";
//
//        // eVar2 (account-specific) - Names for the segments
//        measurement.setEvar(2, "1:beginning,2:second,3:third,4:end");
//
//        // eVar3 (account-specific) -Tracking type
//        measurement.setEvar(3, "video");
//
//        // Added to attempt to force a page view.
//        measurement.track();
//
//        eventEmitter.on(EventType.SET_VIDEO, new EventListener() {
//            @Override
//            public void processEvent(Event event) {
//                ADMS_Measurement measurement = omnitureComponent.getMeasurement();
//                // eVar1 (account-specific) - Video name
//                Video video = (Video)event.properties.get("video");
//                String videoName = video.getStringProperty(Video.Fields.NAME);
//                measurement.setEvar(1, videoName);
//            }
//        });
    }
}
//
//omnitureComponent = new OmnitureComponent(eventEmitter,
//        this,
//        "ovppbrightcove",
//        "ovppartners.d1.sc.omtrdc.net", // "scdebugger.com",
//        "Android Sample App");
//        ADMS_Measurement measurement = omnitureComponent.getMeasurement();
//        measurement.setDebugLogging(true);
//        measurement.setCharSet("UTF-8");
//        ADMS_MediaMeasurement mediaMeasurement = omnitureComponent.getMediaMeasurement();
//        mediaMeasurement.trackSeconds = 8;
//        mediaMeasurement.trackMilestones = "25,50,75";
//        mediaMeasurement.segmentByMilestones = true;
//        mediaMeasurement.trackVars = "eVar1,eVar2,eVar3,prop1,prop2,prop3,prop4";
//
//        // eVar2 (account-specific) - Names for the segments
//        measurement.setEvar(2, "1:beginning,2:second,3:third,4:end");
//
//        // eVar3 (account-specific) -Tracking type
//        measurement.setEvar(3, "video");
//
//        // Added to attempt to force a page view.
//        measurement.track();
//
//        eventEmitter.on(EventType.SET_VIDEO, new EventListener() {
//@Override
//public void processEvent(Event event) {
//        ADMS_Measurement measurement = omnitureComponent.getMeasurement();
//        // eVar1 (account-specific) - Video name
//        Video video = (Video)event.properties.get("video");
//        String videoName = video.getStringProperty(Video.Fields.NAME);
//        measurement.setEvar(1, videoName);
//        }
//        });