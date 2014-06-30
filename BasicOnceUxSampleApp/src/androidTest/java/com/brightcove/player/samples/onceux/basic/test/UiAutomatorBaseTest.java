package com.brightcove.player.samples.onceux.basic.test;

import android.util.Log;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Provides the setUp and tearDown methods for the other tests. Any changes made to this
 * case should always be made to the UiAutomatorTest case.
 * It also serves as an in-depth explanation of the universal setUp and tearDown methods.
 * 
 * @author Paul Michael Reilly -- pmreilly@brightcove.com
 * @author Bryan Gregory Scott -- bscott@brightcove.com
 */
public abstract class UiAutomatorBaseTest extends UiAutomatorTestCase {

    // Class constants
    
    /**
     * The Android logcat tag.
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * The UiObject that represents the Basic ONCE UX Sample App.
     */
    protected UiObject basicOnceUxSampleApp;

    // Public methods.

    /**
     * Test represents a setUp method for the other tests. Using the UiAutomator API, it
     * goes to the home menu, opens up applications, sifts through and opens up the Basic
     * OnceUx Sample App. It asserts that the app with the name that matches "Basic ONCE 
     * UX Sample App" exists. It then opens that application.
     */
    protected void setUp() throws Exception {   

        // Due to the varied user interfaces that android supports, it may be that this 
        // method will not work exactly as intended on every device. For reference, the
        // device this code was written with is a Nexus 10 Tablet using android 4.3.
        
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
        basicOnceUxSampleApp = appViews.getChildByText(new UiSelector().className(android.widget.TextView.class.getName()), "Basic ONCE UX Sample App");
        
        // If it exists, we want to press on the app's icon, launching it.
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
        // Due to the varied user interfaces that android supports, it may be that this 
        // method will not work exactly as intended on every device. For reference, the
        // device this code was written with is a Nexus 10 Tablet using android 4.3.

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
        UiObject settingsApp  = appViews.getChildByText(new UiSelector().className(android.widget.TextView.class.getName()), "Settings");

        // Clicking on the settings app.
        settingsApp.click();
        Log.v(TAG, "Pressing the Settings app.");

        // Then, with settings open we click on the apps tab.
        UiObject settingsAppsTab = new UiObject (new UiSelector().text("Apps"));
        settingsAppsTab.click();
        Log.v(TAG, "Pressing the Apps tab in the Settings App.");

        // Next, we must choose the "Basic ONCE UX Sample App" in the apps settings tab.
        UiObject basicOnceUxSampleAppSettings = new UiObject (new UiSelector().text("Basic ONCE UX Sample App"));
        basicOnceUxSampleAppSettings.click();
        Log.v(TAG, "Pressing the Basic ONCE UX Sample App in the Apps Settings field.");

        // Next, we register the Force stop button and press it to force stop.
        UiObject forceStopButton = new UiObject(new UiSelector().text("Force stop"));
        forceStopButton.clickAndWaitForNewWindow();

        // And register and push the OK button.
        UiObject okButton = new UiObject(new UiSelector().text("OK").className(android.widget.Button.class));
        okButton.click();
       
        // Then press the home button again to leave the settings app.
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the Home button.");

        // The following could serve as an alternative to the first method, or can be used in conjunction 
        // with it. It only works for android devices that have a recent apps button. We want to go to the 
        // recent apps screen to swipe away the recent activity in the Basic ONCE UX Sample App.

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
