package com.brightcove.player.samples.onceux.basic.test;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Provides a class that will check if the Ad Overlay's countdown during 
 * ad breaks matches up the progress bar's counter within a reasonable degree.
 */
public class AdOverlayCountdown extends UiAutomatorTestCase {

    //For Logcat.
    private final String TAG = this.getClass().getSimpleName();

    //Requires private ints/methods that get the two numbers into comparable amounts.

    public void testAdOverlayCountdown() throws UiObjectNotFoundException {
        //Compare Ad Overlay's counter to the progress bar. The two should 
        // add up to at least 29 and no more than 31 for the entirety of 
        // the ad break. This check should occur every second until the ad
        // break has concluded.
    }

}
