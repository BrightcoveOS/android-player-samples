package com.brightcove.player.samples.onceux.basic.test;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Provides the setUp and tearDown methods for the other tests.
 *
 * It also serves as an in-depth explanation of the universal setUp and tearDown methods.

 * Due to the varied user interfaces that android supports, it may be that this 
 * method will not work exactly as intended on every device. The following devices
 * do not work for testing with the setUp as it is currently designed. The 
 * following is a list of known incompatible devices:
 *
 * Samsung Galaxy Tab Pro
 *
 * @author Bryan Gregory Scott -- bscott@brightcove.com
 */
public abstract class OnceUxUiAutomatorBase extends UiAutomatorTestCase {

    // Class constants
    
    /**
     * The Android logcat tag.
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * The UiObject that represents the Basic ONCE UX Sample App.
     */
    protected UiObject basicOnceUxSampleApp;

    // Universal setUp and tearDown methods.

    /**
     * Test represents a setUp method for the other tests. Using the UiAutomator API, it
     * goes to the home menu, opens up applications, sifts through and opens up the Basic
     * OnceUx Sample App. It opens the application with the name that matches "Basic ONCE 
     * UX Sample App."
     */
    protected void setUp() throws Exception {   
        // Simulate a short press on the HOME button and navigate to apps screen.
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the home button.");

        UiObject allAppsButton = new UiObject(new UiSelector().description("Apps"));
        allAppsButton.clickAndWaitForNewWindow();
        Log.v(TAG, "Pressing the All Apps button.");
        
        // Next is the task of navigating to the sample app in the apps menu and scrolling through until it is found.
        UiObject appsTab = new UiObject(new UiSelector().text("Apps"));
        appsTab.click();
        Log.v(TAG, "Pressing the Apps tab.");

        UiScrollable appViews = new UiScrollable(new UiSelector().scrollable(true));
        appViews.setAsHorizontalList();

        // If it exists, we want to press on the app's icon, launching it.
        basicOnceUxSampleApp = appViews.getChildByText(new UiSelector().className(android.widget.TextView.class.getName()), "Basic ONCE UX Sample App");
        if(basicOnceUxSampleApp != null) {
            basicOnceUxSampleApp.clickAndWaitForNewWindow();
            Log.v(TAG, "Pressing the Basic Once Ux Sample App.");
        }
    }
    
    /**
     * In the tearDown, using UiAutomator API, it goes back to home, reopens the 
     * applications menu, sifts through and finds the settings app, and then force 
     * closes the Sample App. Then, it opens up the recent apps screen, and swipes
     * away the Sample App from that screen. By doing so we entirely remove it 
     * from the device's cached memory. This allows us to have a totally clean 
     * environment when beginning a new test.
     */
    @Override protected void tearDown() throws Exception {
        // We now want to leave the app and close it entirely. The first step is to go to the all apps menu and navigate through it.
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the Home button.");

        TimeUnit.SECONDS.sleep(1);
        UiObject allAppsButton = new UiObject(new UiSelector().description("Apps"));
        allAppsButton.clickAndWaitForNewWindow();
        Log.v(TAG, "Pressing the All Apps button.");

        // Next, we have to navigate through the apps menu.
        UiObject appsTab = new UiObject(new UiSelector().text("Apps"));      
        appsTab.click();
        Log.v(TAG, "Pressing the Apps tab.");

        UiScrollable appViews = new UiScrollable(new UiSelector().scrollable(true));
        appViews.setAsHorizontalList();

        // Next, we open the settings app, and open the particular section that specifies settings for Apps.
        UiObject settingsApp  = appViews.getChildByText(new UiSelector().className(android.widget.TextView.class.getName()), "Settings");
        settingsApp.click();
        Log.v(TAG, "Pressing the Settings app.");

        UiObject settingsAppsTab = new UiObject (new UiSelector().text("Apps"));
        settingsAppsTab.click();
        Log.v(TAG, "Pressing the Apps tab in the Settings App.");

        // Next, we must choose the "Basic ONCE UX Sample App".
        UiObject basicOnceUxSampleAppSettings = new UiObject (new UiSelector().text("Basic ONCE UX Sample App"));
        basicOnceUxSampleAppSettings.click();
        Log.v(TAG, "Pressing the Basic ONCE UX Sample App in the Apps Settings field.");

        // And we Force stop the sample app, pressing OK when the clarification prompt appears, and leave settings.
        UiObject forceStopButton = new UiObject(new UiSelector().text("Force stop"));
        forceStopButton.clickAndWaitForNewWindow();

        UiObject okButton = new UiObject(new UiSelector().text("OK").className(android.widget.Button.class));
        okButton.click();

        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the Home button.");

        // The following could serve as an alternative to the first method, or can be used in conjunction 
        // with it. It only works for android devices that have a recent apps button.

        // First, we open the recent apps screen.
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the Home button.");

        getUiDevice().pressRecentApps();
        Log.v(TAG, "Pressing the Recent Apps button.");

        // Then we register the UiObject and swipe it down in order to remove it from the recent activity screen, and return to home.
        UiObject basicOnceUxSampleAppRecentActivity = new UiObject(new UiSelector().description("Basic ONCE UX Sample App"));
        basicOnceUxSampleAppRecentActivity.swipeDown(20);
        Log.v(TAG, "Swiping away the Basic Once Ux Sample App activity Ui Object.");

        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the Home button.");
    }

    // Other Universal Utility Methods

    /**
     * playVideo provides a method that allows for universal access to the play function. It was
     * created as a separate entity to the tests and setUp to help prevent subtle changes from
     * breaking the sample app before function has begun. A universal method helps in this case,
     * and in order to keep the setUp method universal across all test cases, play was kept separate.
     *
     * @throws UiObjectNotFoundException playButtonMissing when the seek controls are hidden.
     */
    protected void playVideo() throws Exception {
        // First, wait for the Sample App to entirely process the video and we tap the screen to reveal the seek controls and press play.
        TimeUnit.SECONDS.sleep(6);
        UiObject playButton = new UiObject(new UiSelector().resourceId("android:id/pause"));
        Log.v(TAG, "Pressing Play...");
        try {
            playButton.click();
        } catch (UiObjectNotFoundException playButtonMissing) {
            Log.v(TAG, "Play button not found. Trying again.");
            toggleSeekControlsVisibility();
            playButton.click();
        }
    }

    /**
     * seekControls provides a method that toggles the accessibility of the seek controls menu,
     * which contains the rewind, fast forward, and pause/play buttons, as well as the seek bar
     * and the Ui Objects that contain the current time elapsed and total time.
     */
    protected void toggleSeekControlsVisibility() {
        Log.v(TAG, "Pressing 500, 500 to toggle the seek controls menu.");
        getUiDevice().click(500, 500);
    }

}
