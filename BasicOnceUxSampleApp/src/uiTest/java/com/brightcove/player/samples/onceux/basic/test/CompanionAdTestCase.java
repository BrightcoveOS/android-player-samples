package com.brightcove.player.samples.onceux.basic.test;

import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import android.util.Log;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiWatcher;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

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
     * Makes the assertion as that there should be a companion ad in an ad break.
     */
    public void testCompanionAd() throws Exception {
        Log.v(TAG, "Beginning testCompanionAd");
        super.playVideo();
        assertTrue("Companion ad not found in preroll ad break.", waitForCompanionAdCheck(msecToPreroll, ADTYPE_PREROLL));
        waitForEndOfAdCheck(ADTYPE_PREROLL);
        assertTrue("Companion ad not found in midroll ad break.", waitForCompanionAdCheck(msecToMidroll, ADTYPE_MIDROLL));
        waitForEndOfAdCheck(ADTYPE_MIDROLL);
        assertTrue("Companion ad not found in postroll ad break.", waitForCompanionAdCheck(msecToPostroll, ADTYPE_POSTROLL));
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
        assertTrue("Ad Break did not begin within given time.", adTextView.waitForExists(msecToPreroll));
        UiObject companionAdUrl = new UiObject(new UiSelector().textContains("starbucks.com"));
        if (companionCheck()) {
            companionAd.clickAndWaitForNewWindow();
        } else {
            fail("Companion ad not found.");
        }
        assertTrue("Companion ad did not link to correct url.", companionAdUrl.waitForExists(15000));
        Log.v(TAG, "Finished testCompanionAdLink");
    }

    /**
     * testCompanionAdVanishPrerolls tests to be sure that the companion ad accompanying the ad
     * breaks vanishes after ad breaks. The test waits for the next ad break to begin, and tests 
     * that ad break's companion ad. If any of the companion ads do not vanish, or the ad breaks
     * do not begin or end on time, then the test will fail.
     */
    public void testCompanionAdVanish() throws Exception {
        Log.v(TAG, "Beginning testCompanionAdVanish");
        super.playVideo();

        // The following tests that the companion ad vanishes after prerolls.
        Log.v(TAG, "Prerolls...");
        waitForCompanionAdCheck(msecToPreroll, ADTYPE_PREROLL);
        assertFalse("Companion ad still present after preroll ad break.", waitForEndOfAdCheck(ADTYPE_PREROLL));

        // Next, the companion ad that accompanies the Midroll ad break is tested.
        Log.v(TAG, "Midrolls...");
        waitForCompanionAdCheck(msecToMidroll, ADTYPE_MIDROLL);
        assertFalse("Companion ad still present after midroll ad break.", waitForEndOfAdCheck(ADTYPE_MIDROLL));

        // Next, the companion ad that accompanies the Postroll ad break is tested.
        Log.v(TAG, "Postrolls...");
        waitForCompanionAdCheck(msecToPostroll, ADTYPE_POSTROLL);
        assertFalse("Companion ad still present after postroll ad break.", waitForEndOfAdCheck(ADTYPE_POSTROLL));
        Log.v(TAG, "Finished testCompanionAdVanish");
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
        if (companionAdFrame.exists()) {
            Log.v(TAG, "Companion ad frame found.");
            try {
                companionAd = companionAdFrame.getChild(new UiSelector().className(android.widget.ImageView.class));
                if (companionAd.exists() && companionAd.isEnabled()) {
                    Log.v(TAG, "Companion ad found.");
                    return true;
                } else {
                    Log.v(TAG, "Companion ad not found.");
                    return false;
                }
            } catch (UiObjectNotFoundException companionAdNotFound) {
                companionAdNotFound.printStackTrace();
                return false;
            }
        } else {
            Log.v(TAG, "Companion ad frame not found.");
            return false;
        }
    }

    /**
     * Waits for adTextView UiObject to be present, then performs companionCheck. If the wait for the ad
     * break times out, then a fail is initiated.
     *
     * @param  int msec the desired number of milliseconds to wait before giving up on the next ad break loading
     * @param  String adType the string identifier for the specific adbreak the method plans to wait to. Only effects logcat.
     * @return the results of companionCheck()
     */
    private boolean waitForCompanionAdCheck(int msec, String adType) throws Exception {
        Log.v(TAG, "Beginning toCompanionAdCheck");
        if (adTextView.waitForExists(msec)) {
            return (companionCheck());
        } else {
            fail("The " + adType + "ad break did not begin in time.");
            return false;
        }
    }

    /**
     * Waits until the CompanionAd is hidden by the seek controls appearing at the end of an ad break,
     * then, it hides them and performs companion check.
     *
     * @param  String adType the string identifier for the specific adbreak the method plans to wait to. Only effects logcat.
     * @return the results of companionCheck()
     */
    private boolean waitForEndOfAdCheck(string adType) throws Exception {
        Log.v(TAG, "Beginning waitForEndOfAdCheck");
        if (companionAd.waitUntilGone(msecAdBreakLength)) {
            super.toggleSeekControlsVisibility();
            return (companionCheck());
        } else { 
            fail("The " + adType + "Ad Break did not complete in time.");
            return false;
        }
    }
}
