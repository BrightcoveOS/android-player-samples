package com.brightcove.player.samples.onceux.basic.test;

import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Provides a test that will check to see if the Sample App can keep track
 * of the playhead position if the user hits the home button while playing
 * or paused, or during specific events in the timeline.
 */
public class LifeCycle extends OnceUxUiAutomatorBaseTestCase {

    /**
     * The Android log tag.
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * The string that contains the playback time before the Life Cycle process.
     */
    private String adTimeStringBeforeLifeCycle;

    /**
     * The string that contains the playback time after the Life Cycle process.
     */
    private String adTimeStringAfterLifeCycle;


    // Test Methods (makes assertions and specifies the location of testing)

    //Tests during Ad Breaks
    /**
     * testLifeCycleAdBreakPlaying tests the life cycle of the app under the circumstances
     * that the user leaves the app by pressing the home button and does not close it in 
     * any way. It calls upon four of the five utility methods. First, it plays the video.
     * Then, it performs lifeCycleInitialCheck, exiting the video, and returning. The video
     * is quickly paused to keep the current time, at which point the time is documented,
     * and then the comparison occurrs. The test asserts stringComparison will return true.
     */
    public void testLifeCycleAdBreakPlaying() throws Exception {
        Log.v(TAG, "Beginning testLifeCycleAdBreakPlaying");
        super.playVideo();
        TimeUnit.SECONDS.sleep(5);
        lifeCycleInitialCheck();
        super.seekControls();
        lifeCycleFollowUpCheck();
        assertTrue("Strings not identical.", stringComparison());
        // Leaving the sample app quickly after re-entering it can cause the sample app to crash.
        // However, if given a moment to compose itself, this can be avoided. Hence the following delay.
        TimeUnit.SECONDS.sleep(5);
        Log.v(TAG, "Finished testLifeCycleAdBreakPlaying");
    }
    /**
     * testLifeCycleAdBreakPaused performs much the same as testLifeCycleAdBreakPlaying,
     * with one exception: it waits five seconds after play has begun, then pauses the
     * video *before* launching lifeCycleInitialCheck. The rest of the process is identical.
     */
    public void testLifeCycleAdBreakPaused() throws Exception {
        Log.v(TAG, "Beginning testLifeCycleAdBreakPaused");
        super.playVideo();
        TimeUnit.SECONDS.sleep(5);
        pauseVideo();
        super.seekControls();
        lifeCycleInitialCheck();
        lifeCycleFollowUpCheck();
        assertTrue("Strings not identical.", stringComparison());
        Log.v(TAG, "Finished testLifeCycleAdBreakPaused");
    }

    //Tests during content blocks
    /**
     * testLifeCycleContentBlockPlaying performs similar to testLifeCycleAdBreakPlaying, but
     * it waits 40 seconds (for the preroll ad to finish), then begins lifeCycleInitialCheck.
     * It follows the same pattern as testLifeCycleAdBreakPlaying for the rest of the check.
     */
    public void testLifeCycleContentBlockPlaying() throws Exception {
        Log.v(TAG, "Beginning testLifeCycleContentBlockPlaying");
        super.playVideo();
        TimeUnit.SECONDS.sleep(45);
        lifeCycleInitialCheck();
        super.seekControls();
        lifeCycleFollowUpCheck();
        assertTrue("Strings not identical.", stringComparison());
        TimeUnit.SECONDS.sleep(5);
        // Leaving the sample app quickly after re-entering it can cause the sample app to crash.
        // However, if given a moment to compose itself, this can be avoided. Hence the following delay.
        Log.v(TAG, "Finished testLifeCycleContentBlockPlaying");
    }
    /**
     * testLifeCycleContentBlockPaused performs exactly like testLifeCycleContentBlockPlaying,
     * but pauses the video before beginning lifeCycleInitialCheck.
     */
    public void testLifeCycleContentBlockPaused() throws Exception {
        Log.v(TAG, "Beginning testLifeCycleContentBlockPaused");
        super.playVideo();
        TimeUnit.SECONDS.sleep(45);
        pauseVideo();
        lifeCycleInitialCheck();
        lifeCycleFollowUpCheck();
        assertTrue("Strings not identical.", stringComparison());
        Log.v(TAG, "Finished testLifeCycleContentBlockPaused");
    }


    // Utility Methods

    /**
     * The pauseVideo utility method provides a way to pause the sample app using the UiAutomator APIs.
     * This is done much the same as playVideo, without the initial waiting for the video to load. It
     * is assumed that this will only be used in conjunction with playVideo, as if otherwise used, this
     * utility method will execute a play function. This is because pause and play functions are both
     * mapped to a single resource id, "android:id/pause".
     */
    private void pauseVideo() throws Exception {
        // First, we bring up the play/seek control menu, then press pause.
        UiObject pauseButton = new UiObject(new UiSelector().resourceId("android:id/pause"));
        super.seekControls();
        Log.v(TAG, "Pressing Pause...");
        try {
            pauseButton.click();
        } catch (UiObjectNotFoundException pauseButtonNotFound) {
            Log.v(TAG, "Pause button not found.");
            pauseButtonNotFound.printStackTrace();
            super.seekControls();
            pauseButton.click();
        }
    }

    /**
     * lifeCycleInitialCheck documents playback location, then exits the app, and reopens
     * it. It heavily uses the UiAutomator API. The playback location is documented by
     * taking hold of the UiObject, and converting its text into a string. It is the first
     * step of the testing process.
     */
    private void lifeCycleInitialCheck() throws Exception {
        Log.v(TAG, "Beginning Life Cycle Check.");
        // First, to make note of the playhead position, we reveal seek controls and examine the text view that has the time elapsed. 
        super.seekControls();
        UiObject adTimeBeforeLifeCycle = new UiObject(new UiSelector().resourceId("android:id/time_current"));
        // Because of the slight inconsistency of the Sample App, we set up try-catch blocks that will be prepared for an exception.
        try {
            adTimeStringBeforeLifeCycle = adTimeBeforeLifeCycle.getText();
        } catch (UiObjectNotFoundException uiPlayheadPositionMissing) {
            Log.v(TAG, "Initial Ad Time not found. Trying again.");
            // This is often as a result of the seek controls (and consequently the playhead location) being hidden, so we will show them and retry.
            super.seekControls();
            adTimeStringBeforeLifeCycle = adTimeBeforeLifeCycle.getText();            
        }
        // Then we leave the app, beginning the check, and return to the app.
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the home button.");
        getUiDevice().pressRecentApps();
        Log.v(TAG, "Pressing the recent apps button.");
        UiObject basicOnceUxSampleAppRecentActivity = new UiObject(new UiSelector().description("Basic ONCE UX Sample App"));
        basicOnceUxSampleAppRecentActivity.clickAndWaitForNewWindow();
        Log.v(TAG, "Reopening the Basic ONCE UX Sample App.");
    }

    /**
     * lifeCycleFollowUpCheck is the second half of the lifeCycleCheck process. It simply
     * searches for the text object that contains the current time of the video, and puts
     * that text into a string.
     */
    private void lifeCycleFollowUpCheck() throws Exception {
        // Now we document our location now that we have returned to the app and enter the text into a string.
        Log.v(TAG, "Beginning the Follow Up check.");
        UiObject adTimeAfterLifeCycle = new UiObject(new UiSelector().resourceId("android:id/time_current"));
        Log.v(TAG, "Getting text from UiObject");
        // Because of the slight inconsistency of the Sample App, we set up try-catch blocks that will be prepared for an exception.
        try {
            adTimeStringAfterLifeCycle = adTimeAfterLifeCycle.getText();
        } catch (UiObjectNotFoundException uiPlayheadPositionMissing) {
            Log.v(TAG, "Follow up Ad Time not found. Trying again.");
            // This is often as a result of the seek controls (and consequently the playhead location) being hidden, so we will show them and retry.
            super.seekControls();
            adTimeStringAfterLifeCycle = adTimeAfterLifeCycle.getText();
        }
    }

    /**
     * The actual comparison of the test is done in stringComparison. The two strings are
     * both divided into two pairs of integers, then each pair is compared to the other
     * pair. If both pairs are identical, stringComparison returns true. Otherwise, it will
     * return false.
     */
    private boolean stringComparison() {
        // The strings are both set to XX:XX, and they must be converted into a suitable format.
        // First, we specify a divider string, which will be used as a means of dividing the two pairs of numbers.
        String divider = ":";
        String[] fragment1 = adTimeStringBeforeLifeCycle.split(divider);
        String[] fragment2 = adTimeStringAfterLifeCycle.split(divider);
        // Now we parse all the integers out of each string, and assign them new int names.
        int minutes1 = Integer.parseInt(fragment1[0]);
        int seconds1 = Integer.parseInt(fragment1[1]);
        int minutes2 = Integer.parseInt(fragment2[0]);
        int seconds2 = Integer.parseInt(fragment2[1]);
        // and we log them, in the event that things go wrong.
        Log.v(TAG, "Before LifeCycle Time: " + minutes1 + divider + seconds1);
        Log.v(TAG, "After LifeCycle Time: " + minutes2 + divider + seconds2);
        // Then, the actual comparison takes place and the boolean returns are specified.
 	if(minutes1 == minutes2 && seconds1 == seconds2) {
            Log.v(TAG, "stringComparison = true. Strings identical.");
            return true;
        } else {
            Log.v(TAG, "stringComparison = false. Strings not identical.");
            return false;
        }
    }
}
