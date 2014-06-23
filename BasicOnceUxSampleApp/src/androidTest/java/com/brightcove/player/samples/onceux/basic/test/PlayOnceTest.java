
package com.brightcove.player.samples.onceux.basic.test;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.content.Context;
import android.media.MediaPlayer;
import android.text.format.Time;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.event.Event;
import com.brightcove.player.samples.onceux.basic.MainActivity;
import com.brightcove.player.samples.onceux.basic.R;
import com.brightcove.player.view.BrightcoveVideoView;
import com.brightcove.plugin.onceux.event.OnceUxEventType;
import com.brightcove.player.display.VideoDisplayComponent;

public class PlayOnceTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private final String TAG = this.getClass().getSimpleName();

    private BrightcoveVideoView brightcoveVideoView;

    private EventEmitter eventEmitter;
    private MainActivity mainActivity;
    private String adUrl = "http://onceux.unicornmedia.com/now/ads/vmap/od/auto/95ea75e1-dd2a-4aea-851a-28f46f8e8195/43f54cc0-aa6b-4b2c-b4de-63d707167bf9/9b118b95-38df-4b99-bb50-8f53d62f6ef8??umtp=0";
    private String contentUrl = "http://cdn5.unicornmedia.com/now/stitched/mp4/95ea75e1-dd2a-4aea-851a-28f46f8e8195/00000000-0000-0000-0000-000000000000/3a41c6e4-93a3-4108-8995-64ffca7b9106/9b118b95-38df-4b99-bb50-8f53d62f6ef8/0/0/105/1438852996/content.mp4";
    private VideoDisplayComponent videoDisplay;
    private int progress;

    final CountDownLatch adDataLatch = new CountDownLatch(1);
    final CountDownLatch didPlayLatch = new CountDownLatch(3);
    final CountDownLatch playLatch = new CountDownLatch(6);
    List<String> actualEventList = new ArrayList<String>();
    String expectedEventArray[] = {EventType.PLAY, OnceUxEventType.AD_DATA_READY, EventType.PLAY, EventType.DID_PLAY};

    /**
     * The PlayOnceTest exists in order to determine if the OnceUx Sample App is following the timeline 
     * it is meant to with a play command issued. The two main aspects to this test are actualEventList 
     * and expectedEventArray. actualEventList documents the events that occur (currently only PLAY,
     * DID_PLAY and AD_DATA_READY) in order to compare them to expectedEventArray, which has a predefined
     * scenario of what events should occur.
     */

    public PlayOnceTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mainActivity = getActivity();
        brightcoveVideoView = (BrightcoveVideoView) mainActivity.findViewById(R.id.brightcove_video_view);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        actualEventList.clear();

        eventEmitter.once(EventType.DID_SET_VIDEO, new EventListener() {
                @Override
                public void processEvent(Event event) {
                    brightcoveVideoView.start();
                }
            });

        eventEmitter.on(OnceUxEventType.AD_DATA_READY, new EventListener() {
                @Override
                public void processEvent(Event event) {
                    actualEventList.add(OnceUxEventType.AD_DATA_READY);
                    adDataLatch.countDown();
                    Log.v(TAG, "AD_DATA_READY event triggered.");
                }
            });

        eventEmitter.on(EventType.PLAY, new EventListener() {
                @Override
                public void processEvent(Event event) {                    
                    actualEventList.add(EventType.PLAY);
                    playLatch.countDown();
                    Log.v(TAG, "PLAY event triggered.");
                }
            });

        eventEmitter.on(EventType.DID_PLAY, new EventListener() {
                @Override
                public void processEvent(Event event) {
                    actualEventList.add(EventType.DID_PLAY);
                    didPlayLatch.countDown();
                    Log.v(TAG, "DID_PLAY event triggered.");
                }
            });
    }

    /**
     * Utility method timelineLoop exists as a framework for the actual comparisons that will be done.
     * timelineLoop checks both the expectedValues array and the actualValues list, comparing the two
     * at each entry. The comparisons stop when the end of the expectedValues array is reached. Then,
     * timelineLoop will check to see if the two are the same size. This is done to see if there are 
     * additional actual events that are unaccounted for, which will allow for edits to the test.
     */
    private void timelineLoop(final String[] expectedValues, final List<String> actualValues) {
        for(int index=0; expectedValues.length > index; index++) {
            Log.v(TAG, "Expected Value is: " + expectedValues[index]);
            Log.v(TAG, "Actual Value is: " + actualValues.get(index));
            assertTrue("Test failed. Expected value different than actual value.", expectedValues[index].equals(actualValues.get(index)));
        }
        assertTrue("Test failed. Different number of expected values than actual values.", expectedValues.length == actualValues.size());
    }

    /**
     * testTimeline is the test method where timelineLoop is called. A 30 second delay is made to allow
     * for the events to be catalogued properly for the actualValues parameter needed for timelineLoop,
     * which, in this case, is actualEventList. Then the comparison takes place and the test ends.
     */
    public void testTimeline() throws InterruptedException {
        eventEmitter.emit(EventType.PLAY);
        final CountDownLatch latch = new CountDownLatch(1);
        latch.await(30, TimeUnit.SECONDS);
        timelineLoop(expectedEventArray, actualEventList);
        brightcoveVideoView.stopPlayback();
    }
}
