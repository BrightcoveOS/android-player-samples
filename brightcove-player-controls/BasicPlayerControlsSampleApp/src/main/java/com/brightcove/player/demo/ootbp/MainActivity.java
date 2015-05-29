/**
 * Copyright (C) 2014 Brightcove Inc. All Rights Reserved. No use,
 * copying or distribution of this work may be made except in
 * accordance with a valid license agreement from Brightcove Inc. This
 * notice must be included on all copies, modifications and
 * derivatives of this work.
 *
 * Brightcove Inc MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BRIGHTCOVE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 *
 * "Brightcove" is a registered trademark of Brightcove Inc.
 */

package com.brightcove.player.demo.ootbp;

import com.brightcove.player.mediacontroller.BrightcoveMediaController;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * This app illustrates the basic behavior of the Android default media controller.
 *
 * @author Paul Michael Reilly
 */
public class MainActivity extends BrightcovePlayer {

    // Private class constants

    private final String TAG = this.getClass().getSimpleName();

    @Override protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.default_activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        brightcoveVideoView.setMediaController(new BrightcoveMediaController(brightcoveVideoView));
        super.onCreate(savedInstanceState);

        // Add a test video from the res/raw directory to the BrightcoveVideoView.
        String PACKAGE_NAME = getApplicationContext().getPackageName();
        Uri video = Uri.parse("android.resource://" + PACKAGE_NAME + "/" + R.raw.shark);
        brightcoveVideoView.add(Video.createVideo(video.toString()));
    }

}
