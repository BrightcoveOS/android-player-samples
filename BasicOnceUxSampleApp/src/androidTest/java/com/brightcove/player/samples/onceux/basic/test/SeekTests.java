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

    //From PreRoll AdBreak seek tests.
    public void testSeekFromPreRollAdToFirstContentBlock() throws UiObjectNotFoundException {

    }
    public void testSeekFromPreRollAdToMidroll() throws UiObjectNotFoundException {

    }
    public void testSeekFromPreRollAdToSecondContentBlock() throws UiObjectNotFoundException {

    }
    public void testSeekFromPreRollAdToPostRollAd() throws UiObjectNotFoundException {

    }

    //From First Content Block seek tests
    public void testSeekFromFirstContentBlockToPreRollAd() throws UiObjectNotFoundException {

    }
    public void testSeekFromFirstContentBlockToMidRollAd() throws UiObjectNotFoundException {

    }
    public void testSeekFromFirstContentBlockToSecondContentBlock() throws UiObjectNotFoundException {

    }
    public void testSeekFromFirstContentBlockToPostRollAd() throws UiObjectNotFoundException {

    }

    //From MidRoll AdBreak seek tests
    public void testSeekFromMidRollAdToPreRoll() throws UiObjectNotFoundException {

    }
    public void testSeekFromMidRollAdToFirstContentBlock() throws UiObjectNotFoundException {

    }
    public void testSeekFromMidRollAdToSecondContentBlock() throws UiObjectNotFoundException {

    }
    public void testSeekFromMidRollAdToPostRollAd() throws UiObjectNotFoundException {

    }

    //From Second Content Break seek tests
    public void testSeekFromSecondContentBlockToPreRollAd() throws UiObjectNotFoundException {

    }
    public void testSeekFromSecondContentBlockToFirstContentBlock() throws UiObjectNotFoundException {

    }
    public void testSeekFromSecondContentBlockToMidRollAd() throws UiObjectNotFoundException {

    }
    public void testSeekFromSecondContentBlockToPostRollAd() throws UiObjectNotFoundException {

    }

    //From PostRoll AdBreak seek tests
    public void testSeekFromPostRollAdToPreRollAd() throws UiObjectNotFoundException {

    }
    public void testSeekFromPostRollAdToFirstContentBlock() throws UiObjectNotFoundException {

    }
    public void testSeekFromPostRollAdToMidRollAd() throws UiObjectNotFoundException {

    }
    public void testSeekFromPostRollAdToSecondContentBlock() throws UiObjectNotFoundException {

    }


}
