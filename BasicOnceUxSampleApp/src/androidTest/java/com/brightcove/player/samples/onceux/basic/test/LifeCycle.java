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

    //Specific problematic locations to test
    public void testLifeCycleBeforeAdDataReady() throws UiObjectNotFoundException {

    }
    public void testLifeCycleEndAdBreak() throws UiObjectNotFoundException {

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
