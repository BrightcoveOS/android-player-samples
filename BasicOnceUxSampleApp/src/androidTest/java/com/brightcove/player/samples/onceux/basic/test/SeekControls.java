package com.brightcove.player.samples.onceux.basic.test;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Provides a class for checking the presence of seek controls. The test 
 * methods revolve around testing during ad breaks and before the preroll.
 */
public class SeekControlsBeforeAd extends UiAutomatorTestCase {

    //For Logcat.
    private final String TAG = this.getClass().getSimpleName();

    public void testSeekControlsBeforeAd() throws UiObjectNotFoundException {

    }

    public void testSeekControlsDuringAdWaitToPlay() throws UiObjectNotFoundException {
        //Waiting to play can cause the controls to prematurely 
        //display despite the ad having not completely played.
    }

    public void testSeekControlsDuringAdImmediatePlay() throws UiObjectNotFoundException {
        // The alternate option to the previous test method.
    }

}
