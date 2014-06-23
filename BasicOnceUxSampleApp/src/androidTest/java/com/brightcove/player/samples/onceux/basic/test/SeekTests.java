package com.brightcove.player.samples.onceux.basic.test;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Provides a class that will perform all manner of seek tests using the 
 * GUI instead of programmatic seeking that was done in previous 
 * instrumentation tests. Test Methods are organized by a From location
 * (where the seek is initialized) and a To location (the seek's destination).
 */
public class SeekTests extends UiAutomatorTestCase {

    //For Logcat.
    private final String TAG = this.getClass().getSimpleName();

    // Utility Method
    private void seekTo() {
        // Used to seek to specific locations to expedite testing by skipping content. To be further defined using the UiAutomator framework.
    }

    private void checkLocation() {
        // Used to differentiate locations (PreRoll, MidRoll, PostRoll and First Content Block vs Second Content Block)
        // Hopefully will reduce the amount of test methods.
    }


    //Test Method
    // Consider skipping majority of content to expedite testing.

    //From PreRoll AdBreak seek tests. 
    // Requires waiting for the ad to start.
    public void testSeekFromPreRollAdToFirstContentBlock() throws UiObjectNotFoundException {
        // Passing test requires the seekTo utility method failing (seeking out of ad break should not be possible).
    }
    public void testSeekFromPreRollAdToMidroll() throws UiObjectNotFoundException {
        // Passing test requires the seekTo utility method failing (seeking out of ad break should not be possible).
    }
    public void testSeekFromPreRollAdToSecondContentBlock() throws UiObjectNotFoundException {
        // Passing test requires the seekTo utility method failing (seeking out of ad break should not be possible).
    }
    public void testSeekFromPreRollAdToPostRollAd() throws UiObjectNotFoundException {
        // Passing test requires the seekTo utility method failing (seeking out of ad break should not be possible).
    }

    //From First Content Block seek tests.
    // Requires waiting for preroll ads to finish and the content to start.
    public void testSeekFromFirstContentBlockToPreRollAd() throws UiObjectNotFoundException {
        // Passing test requires successful seekTo.
    }
    public void testSeekFromFirstContentBlockToMidRollAd() throws UiObjectNotFoundException {
        // Passing test requires an Ad Break interruption with verified location after Ad Break.
        // Location after Ad Break should be immediately following Ad Break (in this instance, ~90 seconds in).
    }
    public void testSeekFromFirstContentBlockToSecondContentBlock() throws UiObjectNotFoundException {
        // Passing test requires an Ad Break interruption with verified location.
        // Location after Ad Break should be immediately following Ad Break (in this instance, the seekTo command's parameter).
    }
    public void testSeekFromFirstContentBlockToPostRollAd() throws UiObjectNotFoundException {
        // Passing test requires two Ad Break interruptions.
    }

    //From MidRoll AdBreak seek tests.
    // Requires waiting for preroll ads to finish and the first content block to finish.
    public void testSeekFromMidRollAdToPreRoll() throws UiObjectNotFoundException {
        // Passing test requires the seekTo utility method failing (seeking out of ad break should not be possible).
    }
    public void testSeekFromMidRollAdToFirstContentBlock() throws UiObjectNotFoundException {
        // Passing test requires the seekTo utility method failing (seeking out of ad break should not be possible).
    }
    public void testSeekFromMidRollAdToSecondContentBlock() throws UiObjectNotFoundException {
        // Passing test requires the seekTo utility method failing (seeking out of ad break should not be possible).
    }
    public void testSeekFromMidRollAdToPostRollAd() throws UiObjectNotFoundException {
        // Passing test requires the seekTo utility method failing (seeking out of ad break should not be possible).
    }

    //From Second Content Break seek tests.
    // Requires waiting for preroll ads, first content block, and midroll ads to finish. 
    public void testSeekFromSecondContentBlockToPreRollAd() throws UiObjectNotFoundException {
        // Passing test requires verified location.
        // Location should be seekTo parameter.
    }
    public void testSeekFromSecondContentBlockToFirstContentBlock() throws UiObjectNotFoundException {
        // Passing test requires verified location.
        // Location should be seekTo parameter.
    }
    public void testSeekFromSecondContentBlockToMidRollAd() throws UiObjectNotFoundException {
        // Passing test requires verified location.
        // Location should be seekTo parameter.
    }
    public void testSeekFromSecondContentBlockToPostRollAd() throws UiObjectNotFoundException {
        // Passing test requires an Ad Break interruption and playback ending.
    }

    //From PostRoll AdBreak seek tests.
    // Requires waiting for preroll ads, first content block, midroll ands, and second content block to finish.
    public void testSeekFromPostRollAdToPreRollAd() throws UiObjectNotFoundException {
        // Passing test requires the seekTo utility method failing (seeking out of ad break should not be possible).
    }
    public void testSeekFromPostRollAdToFirstContentBlock() throws UiObjectNotFoundException {
        // Passing test requires the seekTo utility method failing (seeking out of ad break should not be possible).
    }
    public void testSeekFromPostRollAdToMidRollAd() throws UiObjectNotFoundException {
        // Passing test requires the seekTo utility method failing (seeking out of ad break should not be possible).
    }
    public void testSeekFromPostRollAdToSecondContentBlock() throws UiObjectNotFoundException {
        // Passing test requires the seekTo utility method failing (seeking out of ad break should not be possible).
    }

}
