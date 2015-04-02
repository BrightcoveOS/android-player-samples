BrightcoveExoPlayer-Samples
======================

**_Note: The BrightcoveExoPlayer integration used in these sample applications is in beta._**

## Description

A collection of sample applications using the Brightcove Player SDK for Android with the BrightcoveExoPlayer integration.

These sample applications are built to be modified and run from Android Studio and support the latest Brightcove SDK and plugins.
The following sample applications are included:

* BasicExoPlayerSampleApp: Creates an instance of the BrightcoveExoPlayer integration and demonstrates playback functionality for an MP4 video.

## Features

The BrightcoveExoPlayer integration currently supports the following:
* HLS playback
* EIA-608 embedded captions
* WebVTT and TTML sidecar captions

## Requirements

The BrightcoveExoPlayer integration requires:
* Android version 4.1 and above (API level 16 and up)

The BrightcoveExoPlayer integration is currently developed with:
* Android Studio 1.1.0
* Gradle 2.2.1

Eclipse is not officially supported.

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

## Known Issues

### Playback

#### HLS
* There is currently no support for HLS Live DVR playback with sliding windows.
* There is currently no support for HLSe playback with token authorization.

### Closed Captions
* There is currently no support for multiple sidecar or embedded captions files with multiple locales. You may only provide a single captions file at this time.

### Advertising

### Digital Rights Management
* There is no official support for DRM (Widevine, PlayReady, etc) in this beta.

### Specific Devices Issues
* We have seen a number of problems with video loading and playback on Samsung devices running Android version 4.1 and 4.2. We are actively looking into these issues.

## Reporting Bugs

To report any bugs or otherwise odd behavior found within the BrightcoveExoPlayer integration, please submit the following
to the [Brightcove Native Player SDKs Google Group](https://groups.google.com/forum/#!forum/brightcove-native-player-sdks):

1. Please preface your post with an [ExoPlayer] tag.
  * [ExoPlayer] HLS playback fails with token authorization.
2. Include steps to reproduce the problem.
3. Include the version of Android and manufacturer of the device(s) you are testing on.
4. Include a full logcat of the execution of your sample application demonstrating the problem.
5. Include any screenshots that help to demonstrate the problem.