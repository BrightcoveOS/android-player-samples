package com.brightcove.player.samples.onceux.basic.test;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * This class tests to check if the "Learn More" button is present. One method
 * tests the Preroll adbreak, one checks the Midroll adbreak, where the "Learn
 * More" button should not be present, and one checks the Postroll adbreak.
 */
public class AdOverlayLearnMoreCheck extends UiAutomatorTestCase {

    //For Logcat.
    private final String TAG = this.getClass().getSimpleName();


    // Utility Methods
    private void learnMoreCheck() {
        //Does the actual checking for learn more, will assertTrue for it to be there.
    }
    //Consider adding seekTo utility method to expedite testing.

    private void adBreakCheck() {
        // Determines which ad roll is occurring.
        // If preroll or postroll, there should be a "Learn More" button. If midroll, there should not be a "Learn More" button.
    }

    // Test Method
    public void testAdOverlayLearnMoreCheck() throws UiObjectNotFoundException {
        //Calls upon both utility methods, makes assertions that midroll should not have the "Learn More" ui object, and other ad breaks should.
    }

}
