package com.brightcove.player.samples.onceux.basic.test;

import java.util.concurrent.TimeUnit;
import android.util.Log;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.core.UiDevice;

/**
 * BasicCompanionAdTest tests the presence and functionality of companion ads and their links.
 */
public class CompanionAdTestCase extends OnceUxUiAutomatorBase {


    // Class Constants
    /**
     * The Android logcat tag.
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * The UiObject that represents the companion ad image.
     */
    private UiObject companionAd;

    /**
     * The UiObject that represents the text that is present at every Ad Break. It reads "Your video will
     * resume in" followed by how many seconds are left in the ad break.
     */
    private UiObject adTextView = new UiObject(new UiSelector().textStartsWith("Your video will resume in"));


    // Test Methods
    /**
     * testCompanionAdPresence tests that the companion ad accompanying the ad breaks appears
     * during ad breaks and vanishes after ad breaks have concluded. The test waits for the
     * next ad break to begin, and tests that ad break's companion ad. If any of the companion
     * ads do not vanish, or the ad breaks do not begin or end on time, then the test will fail.
     */
    public void testCompanionAdPresence() throws Exception {
        Log.v(TAG, "Beginning testCompanionAd");
        super.playVideo();
        waitForCompanionAdCheck(msecToPreroll, ADTYPE_PREROLL);
        waitForEndOfAdCheck(ADTYPE_PREROLL);
        waitForCompanionAdCheck(msecToMidroll, ADTYPE_MIDROLL);
        waitForEndOfAdCheck(ADTYPE_MIDROLL);
        waitForCompanionAdCheck(msecToPostroll, ADTYPE_POSTROLL);
        waitForEndOfAdCheck(ADTYPE_POSTROLL);
        Log.v(TAG, "Finished testCompanionAd");
    }

    /**
     * Upon clicking the companion ad, the test assess if browser opens and the correct URL loads.
     * It will wait for the browser's URL text to appear. If it does not appear within 5 seconds,
     * the test will fail. Note that the UiObject representing the browser URL is context-sensitive
     * for the particular ad being used. As a result, the selector should be changed accordingnly
     * if a different ad is used.
     */
    public void testCompanionAdLink() throws Exception {
        Log.v(TAG, "Beginning testCompanionAdLink");
        super.playVideo();
        waitForCompanionAdCheck(msecToPreroll, ADTYPE_PREROLL);
        companionAd.clickAndWaitForNewWindow();
        UiObject companionAdUrl = new UiObject(new UiSelector().textContains("starbucks.com"));
        assertTrue("Companion ad did not link to correct url.", companionAdUrl.waitForExists(15000));
        Log.v(TAG, "Finished testCompanionAdLink");
    }


    // Utility Methods

    /**
     * companionCheck returns <code>true</code> if it finds the companion and, and <code>false</code> 
     * if it does not find it. The method searches for the companion ad frame,
     * then it finds the companion ad itself using the frame as a parent Ui Object.
     *
     * @return true if the companion ad is found, otherwise false.
     */
    private boolean companionCheck() throws Exception {
        Log.v(TAG, "Beginning companionCheck");
        UiObject companionAdFrame = new UiObject(new UiSelector().resourceId("com.brightcove.player.samples.onceux.basic:id/ad_frame"));
        try {
            companionAd = companionAdFrame.getChild(new UiSelector().className(android.widget.ImageView.class));
            assertTrue(companionAd.isEnabled());
            return true;
        } catch (UiObjectNotFoundException companionAdNotFound) {
            return false;
        }
    }

    /**
     * Waits for adTextView UiObject to be present, then asserts true for companionCheck. If the wait
     * for the ad break times out, then the test fails.
     *
     * @param  int msec the desired number of milliseconds to wait before giving up on the next ad break loading
     * @param  String adType the string identifier for the specific adbreak the method plans to wait to. Only effects logcat.
     * @return the results of companionCheck()
     */
    private void waitForCompanionAdCheck(int msec, String adType) throws Exception {
        Log.v(TAG, "Beginning waitForCompanionAdCheck");
        assertTrue("The " + adType + " ad break did not begin in time.", adTextView.waitForExists(msec));
        assertTrue(companionCheck());
        Log.v(TAG, "Finished waitForCompanionAdCheck.");
    }

    /**
     * Waits until the CompanionAd is hidden by the seek controls appearing at the end of an ad break,
     * then, it hides the seek controls and asserts false on companion check. If the wait for the ad break
     * times out, then the test fails.
     *
     * @param  String adType the string identifier for the specific adbreak the method plans to wait to. Only effects logcat.
     * @return the results of companionCheck()
     */
    private void waitForEndOfAdCheck(String adType) throws Exception {
        Log.v(TAG, "Beginning waitForEndOfAdCheck");
        assertTrue("The " + adType + " ad break did not complete in time.", companionAd.waitUntilGone(msecAdBreakLength));
        super.toggleSeekControlsVisibility();
        assertFalse(companionCheck());
        Log.v(TAG, "Finished waitForEndOfAdCheck.");
    }
}
