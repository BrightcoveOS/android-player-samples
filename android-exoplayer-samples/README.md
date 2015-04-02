android-exoplayer-samples
======================

**_Note: The Brightcove ExoPlayer plugin used in these sample applications is in beta._**

## Description

A collection of sample applications using the Brightcove Player SDK for Android with the Brightcove ExoPlayer plugin.

These sample applications are built to be modified and run from Android Studio and support the latest Brightcove SDK and plugins.
The following sample applications are included:

* BasicExoPlayerSampleApp: Creates an instance of the Brightcove ExoPlayer plugin and demonstrates playback functionality for an MP4 video.

## Requirements

The Brightcove ExoPlayer plugin requires:
* Android version 4.1 and above (API level 16 and up)

The Brightcove ExoPlayer plugin is currently developed with Android Studio and Gradle. Eclipse is not officially supported.

## Installation

As demonstrated in the sample applications, adding the following to your applications dependencies will allow the Brightcove ExoPlayer plugin to be invoked:

    dependencies {
          compile "com.brightcove.player:exoplayer:${anpVersion}"
    }

Where *anpVersion* is a gradle.properties property set to indicate the version of the Brightcove Player SDK you are using:

    # Use this property to select the most recent Brightcove Android
    # Native Player version.
    anpVersion=4.3.6

Additionally, you will need to enforce a minimum of API level 16 (Android 4.1+). To do this, add the following to your gradle configuration:

    android {
          defaultConfig {
            minSdkVersion 16
          }
    }

## Quick Start

## Known Issues

## Reporting Bugs

To report any bugs or otherwise odd behavior found within the Brightcove ExoPlayer plugin, please submit the following
to the [Brightcove Native Player SDKs Google Group](https://groups.google.com/forum/#!forum/brightcove-native-player-sdks):

1. Please preface your post with an [ExoPlayer] tag.
⋅⋅* [ExoPlayer] HLS playback fails with token authorization.
2. Include steps to reproduce the problem.
3. Include the version of Android and manufacturer of the device(s) you are testing on.
4. Include a full logcat of the execution of your sample application demonstrating the problem.
