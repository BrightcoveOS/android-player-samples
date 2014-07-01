package com.brightcove.player.samples.onceux.basic.test;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * BasicCompanionAdTest would check for companion ads and their links
 * for when the occasion arrises that they are put into the Sample App.
 */
public class BasicCompanionAdTest extends UiAutomatorTestCase {

    //For Logcat.
    private final String TAG = this.getClass().getSimpleName();

    // Test Methods
    /**
     * Makes the assertion as to whether or not there should be companion ads with the ad breaks.
     */
    public void testCompanionAdCheck() throws UiObjectNotFoundException {
        
    }

    /**
     * Upon clicking the companion ad, the test assess if browser opens and the correct URL loads.
     * Will check the browser's URL when it opens, and assert what it is meant to be.
     */
    public void testCompanionAdLink() throws UiObjectNotFoundException {
        // If companion ad is present, tap
    }

    // Utility Method
    private void companionCheck() {
        // Assessment of companion ad's presence.
    }

    private void adBreakCheck() {
        // Assessment of which adroll is occurring.
    }

}
