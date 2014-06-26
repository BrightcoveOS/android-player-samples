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
 */
public class LearnMoreCheck extends UiAutomatorTestCase {

    //For Logcat.
    private final String TAG = this.getClass().getSimpleName();

    final CountDownLatch noLearnMoreLatch = new CountDownLatch(1);
    final CountDownLatch learnMoreLatch = new CountDownLatch(1);
    final CountDownLatch latch = new CountDownLatch(1);
    private boolean shouldHaveLearnMore;

    protected void setUp() throws Exception {
        // For a more in-depth explanation of the setUp, look in com.brightcove.player.samples.onceux.basic.test.UiAutomatorTest.java
        Log.v(TAG, "Beginning setUp.");
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the home button.");
        UiObject allAppsButton = new UiObject(new UiSelector().description("Apps"));
        allAppsButton.clickAndWaitForNewWindow();
        Log.v(TAG, "Pressing the All Apps button.");
        UiObject appsTab = new UiObject(new UiSelector().text("Apps"));
        appsTab.click();
        Log.v(TAG, "Pressing the Apps tab.");
        UiScrollable appViews = new UiScrollable(new UiSelector().scrollable(true));
        appViews.setAsHorizontalList();
        UiObject basicOnceUxSampleApp = appViews.getChildByText(new UiSelector().className(android.widget.TextView.class.getName()), "Basic ONCE UX Sample App");
        assertTrue("Unable to detect Basic Once Ux Sample app.", basicOnceUxSampleApp != null);
        basicOnceUxSampleApp.clickAndWaitForNewWindow();
        Log.v(TAG, "Pressing the Basic Once Ux Sample App.");

  }   


    // Utility Methods
    private void playVideo() throws Exception {
        getUiDevice().click(500, 500);
        Log.v(TAG, "Pressing 500, 500");
        latch.await(10, TimeUnit.SECONDS);
        getUiDevice().click(500, 500);
        Log.v(TAG, "Pressing 500, 500");
        UiObject playButton = new UiObject(new UiSelector().resourceId("android:id/pause"));
        Log.v(TAG, "Pressing Play...");
        playButton.click();
    }

    private void learnMoreChecker() {
        //Does the actual checking for learn more.
        UiObject learnMoreButton = new UiObject(new UiSelector().text("Learn More >>"));
        if(learnMoreButton.exists()) {
            if(shouldHaveLearnMore == true) {
                Log.v(TAG, "Learn More button found. It should be present.");
                learnMoreLatch.countDown();
            } else {
                Log.v(TAG, "Learn More button found. It should not be present.");
            }
        } else {
            if(shouldHaveLearnMore == false) {
                Log.v(TAG, "Learn More button not found. It should not be present.");
                noLearnMoreLatch.countDown();
            } else {
                Log.v(TAG, "Learn More button not found. It should be present."); 
            }
        }
    }

    // Test Method
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
        assertTrue("Preroll ad break does not have the Learn More Button.", learnMoreLatch.await(2, TimeUnit.MINUTES));
    }

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
        assertFalse("Midroll ad break does have the Learn More Button.", learnMoreLatch.await(3, TimeUnit.MINUTES));
    }

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
        assertTrue("Preroll ad break does not have the Learn More Button.", learnMoreLatch.await(4, TimeUnit.MINUTES));
    }


    @Override protected void tearDown() throws Exception {
        // For an in-depth explanation of this tearDown method, look in UiAutomatorTest.java
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the Home button.");
        getUiDevice().pressRecentApps();
        Log.v(TAG, "Pressing the Recent Apps button.");
        UiObject basicOnceUxSampleAppRecentActivity = new UiObject(new UiSelector().description("Basic ONCE UX Sample App"));
        basicOnceUxSampleAppRecentActivity.swipeDown(20);
        Log.v(TAG, "Swiping away the Basic Once Ux Sample App activity Ui Object.");
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the Home button.");      

        super.tearDown();
    }

}
