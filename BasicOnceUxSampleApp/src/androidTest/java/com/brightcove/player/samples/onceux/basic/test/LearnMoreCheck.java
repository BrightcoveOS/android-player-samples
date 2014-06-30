package com.brightcove.player.samples.onceux.basic.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.util.Log;
import android.content.res.Resources;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;
/**
 * This class tests to check if the "Learn More" button is present. One method
 * tests the Preroll adbreak, one checks the Midroll adbreak, where the "Learn
 * More" button should not be present, and one checks the Postroll adbreak.
 * 
 * @author Bryan Gregory Scott -- bscott@brightcove.com
 */
public class LearnMoreCheck extends UiAutomatorBaseTest {

    /**
     * The Android logcat tag.
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * The latch that keeps track if the Learn More button counts down or not.
     */
    final CountDownLatch learnMoreLatch = new CountDownLatch(1);

    /**
     * Provides an uninhibited way to delay actions.
     */
    final CountDownLatch latch = new CountDownLatch(1);

    /**
     * Provides a quick way to validate IF the learn more button SHOULD be present, as
     * opposed to its actual presence. Will return <code>true</code> or <code>false</code> as instructed 
     * by learnMoreChecker.
     */
    private boolean shouldHaveLearnMore;

    /**
     * The setUp programmatically opens the sample app that has been installed onto 
     * the connected device. This process is explained further and in-depth in the 
     * UiAutomatorBaseTest class file. 
     */
    @Override protected void setUp() throws Exception {
        super.setUp();
    }


    // Utility Methods

    /**
     * playVideo provides a method that allows for universal access to the play function. It was 
     * created as a separate entity to the tests and setUp to help prevent subtle changes from 
     * breaking the sample app before function has begun. A universal method helps in this case,
     * and in order to keep the setUp method universal across all test class, play was kept separate.
     */
    private void playVideo() throws Exception {
        // First, wait for the Sample App to entirely process the video. 10 Seconds is a conservative estimate.
        latch.await(10, TimeUnit.SECONDS);
        // Then, we tap the screen to reveal the seek controls.
        getUiDevice().click(500, 500);
        Log.v(TAG, "Pressing 500, 500 to reveal seek controls and play button.");
        // Next, we press the play button, initiating play.
        UiObject playButton = new UiObject(new UiSelector().resourceId("android:id/pause"));
        Log.v(TAG, "Pressing Play...");
        playButton.click();
    }

    /**
     * learnMoreChecker provides a way to keep track of and call upon the conditional presence of
     * the Learn More button. The tests will make assertions based on waiting for this latch to
     * timeout or count down to zero.
     */
    private void learnMoreChecker() {
        // Establishes the Learn More button.
        UiObject learnMoreButton = new UiObject(new UiSelector().text("Learn More >>"));
        // Checks its existence.
        if(learnMoreButton.exists()) {
            if(shouldHaveLearnMore == true) {
                // If the Learn More button is present, learnMoreLatch counts down.
                Log.v(TAG, "Learn More button found. It should be present.");
                learnMoreLatch.countDown();
            } else {
                Log.v(TAG, "Learn More button found. It should not be present.");
                learnMoreLatch.countDown();
            }
        } else {
            if(shouldHaveLearnMore == false) {
                Log.v(TAG, "Learn More button not found. It should not be present.");
            } else {
                Log.v(TAG, "Learn More button not found. It should be present."); 
            }
        }
    }

    // Test Methods

    /**
     * The Preroll test checks the preroll ad break for the presence of the Learn More button.
     * If the button is present, the test will pass. This is done by calling upon the playVideo
     * utility method to begin, then waiting a few seconds for the ad break to start, before
     * checking for the presence of the UiObject that is present in every ad, "adMarkerText",
     * which reads "Your video will resume in" followed by the number of seconds left in the ad. 
     * If this object is present, then the test will run learnMoreChecker, and assert for the 
     * learnMoreLatch to count down to zero, and wait for the adMarkerText to disappear. If it 
     * does not count down in time, the test will fail.
     */
    public void testLearnMoreCheckPrerolls() throws Exception {
        //Calls upon utility methods, makes assertions that prerolls should have the "Learn More" UiObject.
        playVideo();
        Log.v(TAG, "Beginning check in Preroll ads.");
        shouldHaveLearnMore = true;
        latch.await(10, TimeUnit.SECONDS);
        UiObject adMarkerText = new UiObject(new UiSelector().textStartsWith("Your video will resume in"));
        if(adMarkerText.exists() && adMarkerText.isEnabled()) {
            Log.v(TAG, "Ad Break started.");
            learnMoreChecker();
            adMarkerText.waitUntilGone(30000);
            Log.v(TAG, "Ad Break ended.");
        }
        assertTrue("Preroll ad break does not have the Learn More Button.", learnMoreLatch.await(30, TimeUnit.SECONDS));
    }

    /**
     * The Midroll test checks the preroll ad break for the presence of the Learn More button.
     * If the button is not present, the test will pass. This is done by calling upon the playVideo
     * utility method to begin, then waiting a few seconds for the ad break to start, before
     * checking for the presence of the UiObject that is present in every ad, "adMarkerText",
     * which reads "Your video will resume in" followed by the number of seconds left in the ad. 
     * If this object is present, then the test will run learnMoreChecker, and assert for the 
     * learnMoreLatch to count down to zero, and wait for the adMarkerText to disappear. If it 
     * does not count down in time, the test will fail.
     */
    public void testLearnMoreCheckMidrolls() throws Exception {
        //Calls upon utility methods, makes assertions that midrolls should not have the "Learn More" UiObject.
        playVideo();
        Log.v(TAG, "Beginning check in Midroll ads.");
        shouldHaveLearnMore = false;
        latch.await(70, TimeUnit.SECONDS);
        UiObject adMarkerText = new UiObject(new UiSelector().textStartsWith("Your video will resume in"));
        if(adMarkerText.exists() && adMarkerText.isEnabled()) {
            Log.v(TAG, "Ad Break started.");
            learnMoreChecker();
            adMarkerText.waitUntilGone(30000);
            Log.v(TAG, "Ad Break ended.");
        }
        assertFalse("Midroll ad break does have the Learn More Button.", learnMoreLatch.await(30, TimeUnit.SECONDS));
    }

    /**
     * The Postroll test checks the preroll ad break for the presence of the Learn More button.
     * If the button is present, the test will pass. This is done by calling upon the playVideo
     * utility method to begin, then waiting a few seconds for the ad break to start, before
     * checking for the presence of the UiObject that is present in every ad, "adMarkerText",
     * which reads "Your video will resume in" followed by the number of seconds left in the ad. 
     * If this object is present, then the test will run learnMoreChecker, and assert for the 
     * learnMoreLatch to count down to zero, and wait for the adMarkerText to disappear. If it 
     * does not count down in time, the test will fail.
     */
    public void testLearnMoreCheckPostrolls() throws Exception {
        //Calls upon utility methods, makes assertions that prerolls should have the "Learn More" UiObject.
        playVideo();
        Log.v(TAG, "Beginning check in Postroll ads.");
        shouldHaveLearnMore = true;
        latch.await(3, TimeUnit.MINUTES);
        UiObject adMarkerText = new UiObject(new UiSelector().textStartsWith("Your video will resume in"));
        if(adMarkerText.exists() && adMarkerText.isEnabled()) {
            Log.v(TAG, "Ad Break started.");
            learnMoreChecker();
            adMarkerText.waitUntilGone(30000);
            Log.v(TAG, "Ad Break ended.");
        }
        assertTrue("Postroll ad break does not have the Learn More Button.", learnMoreLatch.await(30, TimeUnit.SECONDS));
    }

    /**
     * The tearDown programmatically force stops the sample app that has been installed onto 
     * the connected device. It then removes the same program from recent applications. This
     * process is explained further in-depth in the UiAutomatorBaseTest class file. 
     */
    @Override protected void tearDown() throws Exception {
        super.tearDown();
    }

}
