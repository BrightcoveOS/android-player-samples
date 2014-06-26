package com.brightcove.player.samples.onceux.basic.test;

import android.util.Log;
import android.widget.*;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Provide a class to provide test cases.
 */
public class UiAutomatorTest extends UiAutomatorTestCase {

    // Private class constants

    // For Logcat.
    private final String TAG = this.getClass().getSimpleName();

    // Public methods.

    public void testCheckForSampleApp() throws Exception {   
        
      // Simulate a short press on the HOME button.
      getUiDevice().pressHome();
      Log.v(TAG, "Pressing the home button.");
      
      // We’re now in the home screen. Next, we want to simulate 
      // a user bringing up the All Apps screen.
      // If you use the uiautomatorviewer tool to capture a snapshot 
      // of the Home screen, notice that the All Apps button’s 
      // content-description property has the value “Apps”.  We can 
      // use this property to create a UiSelector to find the button. 
      UiObject allAppsButton = new UiObject(new UiSelector().description("Apps"));
      
      // Simulate a click to bring up the All Apps screen.
      allAppsButton.clickAndWaitForNewWindow();
      Log.v(TAG, "Pressing the All Apps button.");
      
      // In the All Apps screen, the Settings app is located in 
      // the Apps tab. To simulate the user bringing up the Apps tab,
      // we create a UiSelector to find a tab with the text 
      // label “Apps”.
      UiObject appsTab = new UiObject(new UiSelector().text("Apps"));
      
      // Simulate a click to enter the Apps tab.
      appsTab.click();
      Log.v(TAG, "Pressing the Apps tab.");

      // Next, in the apps tabs, we can simulate a user swiping until
      // they come to the Settings app icon.  Since the container view 
      // is scrollable, we can use a UiScrollable object.
      UiScrollable appViews = new UiScrollable(new UiSelector().scrollable(true));
      
      // Set the swiping mode to horizontal (the default is vertical)
      appViews.setAsHorizontalList();
      
      // Create a UiSelector to find the Settings app and simulate      
      // a user click to launch the app. 
      UiObject basicOnceUxSampleApp = appViews.getChildByText(new UiSelector().className(android.widget.TextView.class.getName()), "Basic ONCE UX Sample App");
      
      // Validate that the Basix OnceUx Sample App exists.
      assertTrue("Unable to detect Basic Once Ux Sample app.", basicOnceUxSampleApp != null);

      // Press on the app's icon, launching it.
      basicOnceUxSampleApp.clickAndWaitForNewWindow();
      Log.v(TAG, "Pressing the Basic Once Ux Sample App.");
  }   

    @Override protected void tearDown() throws Exception {
        // We now want to leave the app and close it entirely. First, 
        // we will press the home button to leave the Sample App.
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the Home button.");
      
        // Next, we will go back to the all apps menu. Our final destination is the settings app.
        UiObject allAppsButton = new UiObject(new UiSelector().description("Apps"));
        allAppsButton.clickAndWaitForNewWindow();
        Log.v(TAG, "Pressing the All Apps button.");

        UiObject appsTab = new UiObject(new UiSelector().text("Apps"));      
        appsTab.click();
        Log.v(TAG, "Pressing the Apps tab.");

        // Next, in the apps tabs, we can simulate a user swiping until
        // they come to the Settings app icon.  Since the container view 
        // is scrollable, we can use a UiScrollable object.
        UiScrollable appViews = new UiScrollable(new UiSelector().scrollable(true));
        
        // Set the swiping mode to horizontal (the default is vertical)
        appViews.setAsHorizontalList();
        appViews.scrollForward();
        Log.v(TAG, "Scrolling forward...");
        appViews.scrollForward();
        Log.v(TAG, "Scrolling forward...");

        // Validate that the settings app is found and exists.
        UiObject settingsApp = appViews.getChildByText(new UiSelector().className(android.widget.TextView.class.getName()), "Settings");
        assertTrue("Unable to detect Settings app.", settingsApp != null);

        // Clicking on the settings app.
        settingsApp.click();
        Log.v(TAG, "Pressing the Settings app.");

        // Then, with settings open we click on the apps tab.
        UiObject settingsAppsTab = new UiObject (new UiSelector().text("Apps"));
        settingsAppsTab.click();
        Log.v(TAG, "Pressing the Apps tab in the Settings App.");

        // Now, we have to register the list object here and scroll down to the Basic Once Ux Sample App
        UiScrollable appSettingsView = new UiScrollable(new UiSelector().scrollable(true));
        appSettingsView.setAsVerticalList();
        appSettingsView.scrollForward();
        Log.v(TAG, "Scrolling forward...");

        // Next, we must choose the "Basic ONCE UX Sample App" in the apps settings tab.
        UiObject basicOnceUxSampleAppSettings = new UiObject (new UiSelector().text("Basic ONCE UX Sample App"));
        basicOnceUxSampleAppSettings.click();
        Log.v(TAG, "Pressing the Basic ONCE UX Sample App in the Apps Settings field.");

        // Next, we register the Force stop button and press it to force stop.
        UiObject forceStopButton = new UiObject(new UiSelector().text("Force stop").className(android.widget.Button.class.getName()));
        forceStopButton.clickAndWaitForNewWindow();

        // And register and push the OK button.
        UiObject okButton = new UiObject(new UiSelector().text("OK").className(android.widget.Button.class));
        okButton.click();
       
        // Then press the home button again to leave the settings app.
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the Home button.");

        // The following is an alternative to the first method, or can be used in conjunction with it.
        // It only works for android devices that have a recent apps button. We want to go to the recent 
        // apps screen to swipe away the recent activity in the Basic ONCE UX Sample App.
        // First, we press home, just in case.
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the Home button.");

        // Then, we press the recent apps button.
        getUiDevice().pressRecentApps();
        Log.v(TAG, "Pressing the Recent Apps button.");

        // Then we register the UiObject and swipe it down in order to remove it from the recent activity screen.
        UiObject basicOnceUxSampleAppRecentActivity = new UiObject(new UiSelector().description("Basic ONCE UX Sample App"));
        basicOnceUxSampleAppRecentActivity.swipeDown(20);
        Log.v(TAG, "Swiping away the Basic Once Ux Sample App activity Ui Object.");

        // Then we return to home.
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the Home button.");
    }
}
