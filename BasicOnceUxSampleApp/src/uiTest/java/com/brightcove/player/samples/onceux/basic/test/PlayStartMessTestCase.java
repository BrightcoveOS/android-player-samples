package com.brightcove.player.samples.onceux.basic.test;

import android.util.Log;
import java.util.concurrent.TimeUnit;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * PlayStartMessTestCase was created in response to a buggy scenario that results when 
 * the play button is pressed the instant the seek controls automatically open. The 
 * scenario consisted of the following: play not being launched despite the play button 
 * being  pressed, the fast forward, rewind, and seek bar being revealed during the ad 
 * break, and a still of the first frame of the video appearing instead of the typical 
 * black space that normally occurred before play had begun.
 * 
 * @author Bryan Gregory Scott -- bscott@brightcove.com
 */
public class PlayStartMessTestCase extends OnceUxUiAutomatorBase {

    /**
     * The Android logcat tag.
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * testPlayStartMessFFWDCheck creates the scenario described in the class level comment,
     * then waits for two seconds for the scenario to initialize. Then, the test asserts the
     * fast forward button will not be present. If it is present, the test will fail.
     */
    public void testPlayStartMessFFWDCheck() throws Exception {
        Log.v(TAG, "Beginning testPlayStartMessFFWDCheck");
        playVideoSpecialized();
        TimeUnit.SECONDS.sleep(2);
        UiObject ffwdButton = new UiObject(new UiSelector().resourceId("android:id/ffwd"));
        assertFalse("Failure: Fast Forward button found.", ffwdButton.waitForExists(10000));
    }

    /**
     * testPlayStartMessREWCheck creates the scenario described in the class level comment,
     * then waits for two seconds for the scenario to initialize. Then, the test asserts the 
     * rewind button will not be there. If it is present, the test will fail.
     */
    public void testPlayStartMessREWCheck() throws Exception {
        Log.v(TAG, "Beginning testPlayStartMessREWCheck");
        playVideoSpecialized();
        TimeUnit.SECONDS.sleep(2);
        UiObject rewButton = new UiObject(new UiSelector().resourceId("android:id/rew"));
        assertFalse("Failure: Rewind button found.", rewButton.waitForExists(10000));
    }

    /**
     * testPlayStartMessSeekBarCheck creates the scenario described in the class level comment,
     * then waits for two seconds for the scenario to initialize. Then, the test checks asserts
     * the seek bar will be there. If it is present, the test will fail.
     */
    public void testPlayStartMessSeekBarCheck() throws Exception {
        Log.v(TAG, "Beginning testPlayStartMessSeekBarCheck");
        playVideoSpecialized();
        TimeUnit.SECONDS.sleep(2);
        UiObject seekBar = new UiObject(new UiSelector().resourceId("android:id/mediacontroller_progress"));
        assertFalse("Failure: Seek Bar found.", seekBar.waitForExists(10000));
    }

    /**
     * testPlayStartMessCompanionAd creates the scenario described in the class level comment,
     * then waits for two seconds for the scenario to initialize. Then, the test toggles seek 
     * control viewability and asserts that the companion ad will not be there. The companion
     * ad was implemented after this scenario was discovered, but it also appeared when the 
     * scenario is initialized.
     */
    public void testPlayStartMessCompanionAd() throws Exception {
        Log.v(TAG, "Beginning testPlayStartMessCompanionAd");
        playVideoSpecialized();
        TimeUnit.SECONDS.sleep(2);
        super.toggleSeekControlsVisibility();
        UiObject companionAd = new UiObject(new UiSelector().resourceId("com.brightcove.player.samples.onceux.basic:id/ad_frame"));
        assertFalse("Failure: Companion ad found.", companionAd.waitForExists(10000));
    }

    /**
     * The scenario as described in the class level comment occurs based on an outdated 
     * version of the super.playVideo method. As a result, that version needed to be 
     * preserved here for testing that scenario.
     */
    private void playVideoSpecialized() throws Exception {
        UiObject playButton = new UiObject(new UiSelector().resourceId("android:id/pause"));
        playButton.waitForExists(6000);
        Log.v(TAG, "Pressing Play...");
        try {
            playButton.click();
        } catch (UiObjectNotFoundException playButtonMissing) {
            playButtonMissing.printStackTrace();
            Log.v(TAG, "Play button not found. Trying again.");
            super.toggleSeekControlsVisibility();
            playButton.click();
        }
    }

}
