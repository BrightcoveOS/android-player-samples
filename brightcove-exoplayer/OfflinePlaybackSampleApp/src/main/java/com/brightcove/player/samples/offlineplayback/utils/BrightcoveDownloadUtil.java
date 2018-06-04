package com.brightcove.player.samples.offlineplayback.utils;

import android.os.Bundle;
import android.util.Log;

import com.brightcove.player.model.MediaFormat;
import com.brightcove.player.model.Video;
import com.brightcove.player.offline.MediaDownloadable;

import java.util.ArrayList;

/**
 * A download util class to select audio and caption tracks
 */
public class BrightcoveDownloadUtil {

    private static final String TAG = BrightcoveDownloadUtil.class.getSimpleName();

    private BrightcoveDownloadUtil() {

    }


    public static void selectMediaFormatTracksAvailable(final Video video, MediaDownloadable mediaDownloadable, Bundle bundle) {
        boolean didListChange;

        ArrayList<MediaFormat> audio = bundle.getParcelableArrayList(MediaDownloadable.AUDIO_LANGUAGES);
        int indexMain = -1;
        ArrayList<MediaFormat> newAudio = new ArrayList<>();

        if (audio != null && audio.size() > 0) {
            Log.v(TAG, "Adding the \"main\" audio track.");
            //First lets find the index of the audio with role main
            ArrayList<String> roles = bundle.getStringArrayList(MediaDownloadable.AUDIO_LANGUAGE_ROLES);
            for (int i = 0; i < roles.size(); i++) {
                if ("main".equalsIgnoreCase(roles.get(i))) {
                    indexMain = i;
                    break;
                }
            }

            //If indexMain equals -1, there was no main so we'll pick the first one
            if (indexMain == -1) {
                indexMain = 0;
            }

            //Select main
            newAudio.add(audio.get(indexMain));
        }

        // Now select the "extra" audio track
        if (audio.size() > 1) {
            Log.v(TAG, "Alternate audio track download allowed for this video. Adding an \"alternate\" audio track");
            if (indexMain == 0) {
                //The first audio is also the main, so let's pick the second audio
                newAudio.add(audio.get(1));
            } else {
                //We'll pick the first audio as the extra
                newAudio.add(audio.get(0));
            }
        } else {
            Log.v(TAG, "Alternate audio track download allowed, but there were no \"alternate\" audio tracks to select.");
        }
        bundle.putParcelableArrayList(MediaDownloadable.AUDIO_LANGUAGES, newAudio);
        didListChange = true;

        ArrayList<MediaFormat> captions = bundle.getParcelableArrayList(MediaDownloadable.CAPTIONS);
        Log.v(TAG, "Captions array size: " + captions.size());
        if (captions != null && captions.size() > 0) {
            ArrayList<MediaFormat> newCaptions = new ArrayList<>();
            Log.v(TAG, "Adding the first caption track as the \"default\" caption track.");
            newCaptions.add(captions.get(0));

            if (captions.size() > 1) {
                Log.v(TAG, "Adding the first of the remaining caption tracks as the \"alternate\" caption track.");
                newCaptions.add(captions.get(1));
            } else {
                Log.v(TAG, "Captions size is not GT 1, no alternate captions will be downloaded, even though allowed.");
            }

            bundle.putParcelableArrayList(MediaDownloadable.CAPTIONS, newCaptions);
            didListChange = true;
        }

        // Why are we checking if the list change?
        // Because if we always set the bundle to the MediaDownloadble, we're telling to downloading every audio and every caption.
        if (didListChange) {
            mediaDownloadable.setConfigurationBundle(bundle);
        }
    }
}
