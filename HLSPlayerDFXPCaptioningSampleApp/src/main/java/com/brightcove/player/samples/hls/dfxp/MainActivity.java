package com.brightcove.player.samples.hls.dfxp;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.VideoListener;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.SeamlessVideoView;

/**
 * This app illustrates how to use the Brightcove HLS player for
 * Android, with DFXP Captions.
 *
 * @author Billy Hnath (original code)
 * @author Jim Whisenant (adapted this example from DFXPClosedCaptioningSampleApp)
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
        brightcoveVideoView = (SeamlessVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        Catalog catalog = new Catalog("UV3EUeje-jlI5sUpJAGsDZ2jki26BZl78pRKemVDxNTXAxyVOabPdA..");
        catalog.findVideoByReferenceID("10sec-en-xml-caps", new VideoListener() {
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
            }

            public void onError(String error) {
                Log.e(TAG, error);
            }
        });

        // Log whether or not instance state in non-null.
        if (savedInstanceState != null) {
            Log.v(TAG, "Restoring saved position");
        } else {
            Log.v(TAG, "No saved state");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cc_settings:
                showClosedCaptioningDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}