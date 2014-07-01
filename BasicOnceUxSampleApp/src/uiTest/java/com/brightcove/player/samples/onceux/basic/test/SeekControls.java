package com.brightcove.player.samples.onceux.basic.test;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Provides a class for checking the presence of seek controls. The test 
 * methods organization revolves around testing during ad breaks and before 
 * the preroll.
 */
public class SeekControls extends UiAutomatorTestCase {

    //For Logcat.
    private final String TAG = this.getClass().getSimpleName();

    // Utility Method
    public void seekController() {
        //Steps:
        // simulate a tap on screen to bring up the controls.
        // check for the presence of fastforward/rewind buttons, and the progress
        // if present, fail.
        // consider another separate class to test the results of pressing any of the objects, should they be present.
    }

    // Test Methods
    public void testSeekControlsBeforeAd() throws UiObjectNotFoundException {
        //Do not wait for anything before calling on seekController.
    }

    public void testSeekControlsDuringAdWaitToPlay() throws UiObjectNotFoundException {
        //Waiting to play can cause the controls to prematurely display despite the ad having not completely played. Requires programmatic delay. Then call upon seekController.
    }

    public void testSeekControlsDuringAdImmediatePlay() throws UiObjectNotFoundException {
        //The alternate option to the previous test method, where play is initialized as soon as possible.
    }

}
