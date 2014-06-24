package com.brightcove.player.samples.onceux.basic.test;

import java.util.concurrent.CountDownLatch;
import android.util.Log;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventType;
import com.brightcove.player.event.EventListener;

import com.brightcove.player.samples.onceux.basic.MainActivity;
import com.brightcove.player.samples.onceux.basic.R;
import com.brightcove.player.view.BrightcoveVideoView;
import com.brightcove.plugin.onceux.event.OnceUxEventType;


/**
 * This class tests to check if the "Learn More" button is present. One method
 * tests the Preroll adbreak, one checks the Midroll adbreak, where the "Learn
 * More" button should not be present, and one checks the Postroll adbreak.
 */
public class AdOverlayLearnMoreCheck extends UiAutomatorTestCase {

    //For Logcat.
    private final String TAG = this.getClass().getSimpleName();

    final CountDownLatch learnMoreLatch = new CountDownLatch(2);
    final CountDownLatch adBreakLatch = new CountDownLatch(3);

    private BrightcoveVideoView brightcoveVideoView;
    private EventEmitter eventEmitter;
    private MainActivity mainActivity;

    protected void setup() throws Exception {
        // Enabling eventEmitter use.
        brightcoveVideoView = (BrightcoveVideoView) mainActivity.findViewById(R.id.brightcove_video_view);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        // For a more in-depth explanation of the rest of the setUp, look in com.brightcove.player.samples.onceux.basic.test.UiAutomatorTest.java
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

    @Override protected void tearDown() throws Exception {
        // For a more in-depth explanation of this tearDown method, look in com.brightcove.player.samples.onceux.basic.test.UiAutomatorTest.java
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the All Apps button.");
        getUiDevice().pressRecentApps();
        Log.v(TAG, "Pressing Recent Apps button.");
        UiObject basicOnceUxSampleAppRecentMode = new UiObject(new UiSelector().description("Basic ONCE UX Sample App"));
        if(basicOnceUxSampleAppRecentMode.exists()) {
            basicOnceUxSampleAppRecentMode.longClick();
            Log.v(TAG, "Long Pressing the Basic OnceUx Sample App Recent Mode button.");
            UiObject removeFromList = new UiObject(new UiSelector().text("Remove from list"));
            removeFromList.click();
            Log.v(TAG, "Pressing the Remove From List button");
        }
        getUiDevice().pressHome();
        Log.v(TAG, "Pressing the Home button.");
      
    }

}
