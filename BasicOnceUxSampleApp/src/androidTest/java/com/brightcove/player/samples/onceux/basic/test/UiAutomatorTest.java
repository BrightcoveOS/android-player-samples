package com.brightcove.player.samples.onceux.basic.test;

import android.util.Log;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Provide a test case that tests the setUp method for the other tests. 
 * It also serves as an in-depth explanation of the universal setUp and tearDown methods.
 * 
 * @author Paul Michael Reilly -- pmreilly@brightcove.com
 * @author Bryan Gregory Scott -- bscott@brightcove.com
 */
public class UiAutomatorTest extends UiAutomatorBaseTest {

    final CountDownLatch latch = new CountDownLatch(1);

    /**
     * The setUp programmatically opens the sample app that has been installed onto 
     * the connected device. This process is explained further and in-depth in the 
     * UiAutomatorBaseTest class file. 
     */
    @Override protected void setUp() throws Exception {
        super.setUp();
    }

    // Test Methods

    /**
     * testCheckForSampleApp ensures that the setUp method being used for the other 
     * tests is functioning correctly and finding the Sample App.
     */
    public void testCheckForSampleApp() throws Exception {
        // Identified as a way to establish the success of the setUp method.
        latch.await(5, TimeUnit.SECONDS);
        assertTrue("Unable to detect Basic Once Ux Sample app.", basicOnceUxSampleApp != null);
    }
    //TODO: Come up with a way to test the presence of objects in the super.tearDown method.
    // UiObjects that need testing include: forceStopButton, basicOnceUxSampleAppSettings,
    // and settingsApp.

    /**
     * The tearDown programmatically force stops the sample app that has been installed onto 
     * the connected device. It then removes the same program from recent applications. This
     * process is explained further in-depth in the UiAutomatorBaseTest class file. 
     */
    @Override protected void tearDown() throws Exception {
        super.tearDown();
    }

}
