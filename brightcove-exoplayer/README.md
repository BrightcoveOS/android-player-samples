BrightcoveExoPlayer
======================

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
* Ad Integrations
 * OnceUX
 * FreeWheel
 * Google IMA
* Analytics
 * Brightcove
 * Omniture
* Android TV
* Video 360

## Requirements

The BrightcoveExoPlayer integration requires:
* Android version 4.1 and above (API level 16 and up)

The BrightcoveExoPlayer sample applications are currently developed with:
* Android Studio 1.1.0
* Gradle 2.4

Using the Eclipse IDE is not officially supported.

## Installation

First, make sure you have the Brightcove repository added:

    # build.gradle

    repositories {
          maven {
              url "http://repo.brightcove.com/releases"
        }
    }

As demonstrated in the sample applications, adding the following to your applications dependencies will allow the BrightcoveExoPlayer integration to be invoked:

    # build.gradle

    dependencies {
          compile "com.brightcove.player:exoplayer:${anpVersion}"
    }

Where *anpVersion* is a gradle.properties property set to indicate the version of the Brightcove Player SDK you are using:

    # gradle.properties

    # Use this property to select the most recent Brightcove Android
    # Native Player version.
    anpVersion=4.6+

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
            super.onCreate(savedInstanceState);
    [2]     setContentView(R.layout.activity_main);

            // Add a test video to the BrightcoveExoPlayerVideoView.
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
 2. Instantiates the BrightcoveExoPlayerVideoView from the layout XML set with *setContentView()*.
 3. Loads a sample video from the Media API of Brightcove VideoCloud, given an authorization token and a video id.
 4. An XML declaration for the BrightcoveExoPlayerVideoView from the activity's layout file.

## Limitations

#### HLS
* There is currently no support for HLS Live DVR playback with sliding windows.

#### DRM
* DRM is not supported in Android versions prior to 4.3. For more information, please refer to the [ExoPlayer Developer
Forum](http://google.github.io/ExoPlayer/guide.html#digital-rights-management).

#### Closed Captions
* There is currently no support for multiple sidecar or embedded captions files with multiple locales. You may only provide a single captions file at this time.

## Reporting Bugs and Submitting Feedback

To report any bugs found within the BrightcoveExoPlayer integration, please submit the following to the [Brightcove Native Player SDKs Google Group](https://groups.google.com/forum/#!forum/brightcove-native-player-sdks):

1. Please preface your post with an [ExoPlayer] tag.
2. Include steps to reproduce the problem.
3. Include the version of Android and manufacturer of the device(s) you are testing on.
4. Include a full logcat of the execution of your sample application demonstrating the problem.
5. Include any screenshots that help to demonstrate the problem.
