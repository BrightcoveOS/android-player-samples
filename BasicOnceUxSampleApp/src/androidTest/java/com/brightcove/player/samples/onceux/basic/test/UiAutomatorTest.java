package com.brightcove.player.samples.onceux.basic.test;

import android.util.Log;

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

    public void testCheckForSampleApp() throws UiObjectNotFoundException {   
        
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

    public void tearDown() throws Exception {
      // We now want to leave the app and close it entirely. First, 
      // we will press the home button to leave the Sample App.
      getUiDevice().pressHome();
      Log.v(TAG, "Pressing the All Apps button.");

      // Next, we will bring up the recent apps screen.
      getUiDevice().pressRecentApps();
      Log.v(TAG, "Pressing Recent Apps button.");
      
      // Now, we will need to find the UiObject that represents the activity
      // of the Basic Once Ux Sample App and swipe it away.
      UiObject basicOnceUxSampleAppRecentMode = new UiObject(new UiSelector().description("Basic ONCE UX Sample App"));
      if(basicOnceUxSampleAppRecentMode.exists()) {
          // If it exists, we do a long click that will bring up the options for the list.
          basicOnceUxSampleAppRecentMode.longClick();
          Log.v(TAG, "Long Pressing the Basic OnceUx Sample App Recent Mode button.");
          
          // Then, we click that button and it removes the app from the list. 
          UiObject removeFromList = new UiObject(new UiSelector().text("Remove from list"));
          removeFromList.click();
          Log.v(TAG, "Pressing the Remove From List button");
      }

      // Then press the home button again to leave the recent apps menu.
      getUiDevice().pressHome();
      Log.v(TAG, "Pressing the Home button.");

    }

}
