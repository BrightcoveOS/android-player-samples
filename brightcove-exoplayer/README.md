BrightcoveExoPlayer
======================

**_Note: The BrightcoveExoPlayer integration used in these sample applications is in beta._**

## Description

A collection of sample applications using the Brightcove Player SDK for Android with the BrightcoveExoPlayer integration.

These sample applications are built to be modified and run from Android Studio and support the latest Brightcove SDK and plugins.
The following sample applications are included:

* BasicSampleApp: Creates an instance of the BrightcoveExoPlayer integration and demonstrates playback functionality.

## Features

The BrightcoveExoPlayer integration currently supports the following:
* Media Playback
 * HLS
* Closed Captioning
 * EIA-608
 * WebVTT
 * DFXP/TTML

## Requirements

The BrightcoveExoPlayer integration requires:
* Android version 4.1 and above (API level 16 and up)

The BrightcoveExoPlayer sample applications are currently developed with:
* Android Studio 1.1.0
* Gradle 2.2.1

Using the Eclipse IDE is not officially supported.

## Installation

As demonstrated in the sample applications, adding the following to your applications dependencies will allow the BrightcoveExoPlayer integration to be invoked:

    # build.gradle

    dependencies {
          compile "com.brightcove.player:exoplayer:${anpVersion}"
    }

Where *anpVersion* is a gradle.properties property set to indicate the version of the Brightcove Player SDK you are using:

    # gradle.properties

    # Use this property to select the most recent Brightcove Android
    # Native Player version.
    anpVersion=4.3+

Additionally, you will need to enforce a minimum of API level 16 (Android 4.1+). To do this, add the following to your gradle configuration:

    # build.gradle

    android {
          defaultConfig {
            minSdkVersion 16
          }
    }

## Quick Start

The BrightcoveExoPlayer integration serves as a bridge between [Google's ExoPlayer](https://github.com/google/ExoPlayer) and the [Brightcove Native Player SDK for Android](http://docs.brightcove.com/en/video-cloud/mobile-sdks/brightcove-player-sdk-for-android/index.html).
The following example is taken from the BasicSampleApp:

        // MainActivity.java
    [1] public class MainActivity extends BrightcovePlayer {

        private final String TAG = this.getClass().getSimpleName();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
            // entering the superclass. This allows for some stock video player lifecycle
            // management.  Establish the video object and use it's event emitter to get important
            // notifications and to control logging.
            setContentView(R.layout.activity_main);
    [2]     brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
            super.onCreate(savedInstanceState);

            // Add a test video to the BrightcoveVideoView.
    [3]     Catalog catalog = new Catalog("ZUPNyrUqRdcAtjytsjcJplyUc9ed8b0cD_eWIe36jXqNWKzIcE6i8A..");
            catalog.findVideoByID("4147927164001", new VideoListener() {
                @Override
                public void onVideo(Video video) {
                    brightcoveVideoView.add(video);
                }

                @Override
                public void onError(String s) {
                    Log.e(TAG, "Could not load video: " + s);
                }
            });

            // Log whether or not instance state in non-null.
            if (savedInstanceState != null) {
                Log.v(TAG, "Restoring saved position");
            } else {
                Log.v(TAG, "No saved state");
            }
        }

        // res/layout/activity_main.xml
    [4] <com.brightcove.player.view.BrightcoveExoPlayerVideoView
            android:id="@+id/brightcove_video_view"
            android:layout_width="match_parent"
            android:layout_height="280dp"
            android:layout_gravity="center_horizontal|top"/>

To explain in more detail:
 1. Extends *MainActivity* to use the *BrightcovePlayer* class, which handles activity lifecycle behavior for the Brightcove player used.
 2. Instantiates the BrightcoveExoPlayerVideoView from the layout XML set with *setContentView()* and assigns to the *brightcoveVideoView* member variable of the *BrightcovePlayer* class.
 3. Loads a sample video from the Media API of Brightcove VideoCloud, given an authorization token and a video id.
 4. A XML declaration for the BrightcoveExoPlayerVideoView from the activity's layout file.



## Known Issues

### Playback

#### HLS
* There is currently no support for HLS Live DVR playback with sliding windows.
* There is currently no support for HLSe playback with token authorization.

### Closed Captions
* There is currently no support for multiple sidecar or embedded captions files with multiple locales. You may only provide a single captions file at this time.

### Advertising
We are investigating user interface issues in relation to advertising:
* A progress bar artifact still visible if seeking past midroll ads, when the ad itself plays
* After a preroll ad plays, the scrubber will sometimes be set to a point in the player's progress bar equal to the duration of the ad itself.

### Digital Rights Management
* There is no official support for DRM (Widevine, PlayReady, etc) in this beta.

### Device- and OS Level-Specific Issues
We are actively investigating a number of device- and OS Level-specific issues found in testing:
* We have seen a number of problems with video loading and playback on Samsung devices running Android version 4.1 and 4.2.
* We have seen an issue where the video progress timer and video duration display do not load on devices running Android version 4.1.1.
* We have seen issues where bringing the player back to the foreground (for example, after tapping the "Learn More" button when an ad is playing) can cause an application leak, which can in turn cause the video to restart.

## Reporting Bugs and Submitting Feedback

To report any bugs or otherwise odd behavior found within the BrightcoveExoPlayer integration, please submit the following
to the [Brightcove Native Player SDKs Google Group](https://groups.google.com/forum/#!forum/brightcove-native-player-sdks):

1. Please preface your post with an [ExoPlayer] tag.
  * [ExoPlayer] HLS playback fails with token authorization.
2. Include steps to reproduce the problem.
3. Include the version of Android and manufacturer of the device(s) you are testing on.
4. Include a full logcat of the execution of your sample application demonstrating the problem.
5. Include any screenshots that help to demonstrate the problem.
