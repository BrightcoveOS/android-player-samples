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
public class AdOverlayLearnMore extends UiAutomatorTestCase {

    //For Logcat.
    private final String TAG = this.getClass().getSimpleName();

    public void testAdOverlayLearnMorePreRoll() throws UiObjectNotFoundException {

    }

    public void testAdOverlayLearnMoreMidRoll() throws UiObjectNotFoundException {
        //Midroll Ad Break should not have the Learn More button.
    }

    public void testAdOverlayLearnMorePostRoll() throws UiObjectNotFoundException {

    }

}
