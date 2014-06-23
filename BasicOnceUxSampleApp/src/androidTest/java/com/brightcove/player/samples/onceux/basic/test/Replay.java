package com.brightcove.player.samples.onceux.basic.test;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Provides a class to test if the Sample App's replay functionality 
 * works correctly, in terms of actually playing over from the 
 * beginning after play has stopped entirely.
 */
public class Replay extends UiAutomatorTestCase {

    //For Logcat.
    private final String TAG = this.getClass().getSimpleName();

    // Utility Methods

    private void seekTo() {
        // Used to seek to specific locations to expedite testing by skipping content. To be further defined using the UiAutomator framework.
    }
    private void firstTimeWatch() {
        // To be called in testReplay.
        //Steps: 
        // Wait for ads to load and prerolls to complete
        // seekTo the end of the content
        // Wait for final EndAdBreak event
        // Wait for stop/endContent event
    }

    // Test Methods
    public void testReplay() throws UiObjectNotFoundException {
        //Steps:
        // Press Play to restart play
        // if the play function does not work, or play resumes from somewhere that is not the beginning of the video, fail.
        // test that ads are skippable
        // if the seek controls are hidden, fail.
    }

}
