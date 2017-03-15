package com.brightcove.player.samples.onceux.basic.test;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Provides a class to test that the Learn More button in the Ad Overlay
 * opens a new window in the browser and goes to the correct URL.
 */
public class PressLearnMore extends UiAutomatorTestCase {

    //For Logcat.
    private final String TAG = this.getClass().getSimpleName();

    // Utility Methods
    private void learnMoreCheck() {
        //Does the actual checking for the "Learn More" ui object.
    }

    // Test Method
    public void testAdOverlayPressLearnMore() throws UiObjectNotFoundException {
        // Does actual pressing off "Learn More" button
        // Assess if browser opens, and if the correct URL has loaded.
    }

}
