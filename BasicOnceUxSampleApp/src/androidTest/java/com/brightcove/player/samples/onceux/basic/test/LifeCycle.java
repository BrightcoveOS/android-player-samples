package com.brightcove.player.samples.onceux.basic.test;

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
public class LifeCycle extends UiAutomatorTestCase {

    //For Logcat.
    private final String TAG = this.getClass().getSimpleName();

    //Utility Methods

    private void seekTo() {
        // Used to seek to specific locations. To be further defined using the UiAutomator framework.
    }

    private void lifeCycleCheck() {
        //Steps:
        // makes note of playheadPosition
        // simulates a home button press
        // simulates a "recent apps" button press
        // reopens the Sample App
        // checks location and pause/play state.
    }

    // Test Methods (makes assertions and specifies the location of testing)

    //Specific problematic locations to test
    public void testLifeCycleBeforeAdDataReady() throws UiObjectNotFoundException {
        // The period after the video is set and before the ad has loaded was
        // identified as a problematic area in earlier testing where pressing
        // the playback control buttons would cause the entire sample app to 
        // crash, and for the AdDataReady event to never trigger nor play to
        // ever initialize.
    }
    public void testLifeCycleEndAdBreak() throws UiObjectNotFoundException {
        // The period during an EndAdBreak event and the moments thereafter
        // were identified as problematic location due to the buggy nature
        // of the issue and could cause problems with returning to the video
        // in the correct location, as opposed to the equivalent time with a 
        // different total time or current location. Should be initiated by 
        // and END_AD_BREAK event.
    }

    //Tests during Ad Breaks
    public void testLifeCycleAdBreakPlaying() throws UiObjectNotFoundException {

    }
    public void testLifeCycleAdBreakPaused() throws UiObjectNotFoundException {

    }

    //Tests during content blocks
    public void testLifeCycleContentBlockPlaying() throws UiObjectNotFoundException {

    }
    public void testLifeCycleContentBlockPaused() throws UiObjectNotFoundException {

    }

}
