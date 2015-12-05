Brightcove Player Controls for Android - Samples
======================

## Description

A collection of sample applications using the Brightcove Player SDK with the Brightcove Player Controls integration.

These sample applications are built to be modified and run from Android Studio and support the latest Brightcove SDK and plugins.

## Features

The Brightcove Player Controls integration currently supports the following:
* Customizable Font-Awesome icons
* Rewind, Captions and Full Screen by default
* Mirrors the style of the new Brightcove Web Player
* Fade and Slide animation styles for showing and hiding the Brightcove Player Controls

## Requirements

The Brightcove Player Controls integration requires:
* Android version 2.3.3 and above (API level 10 and up)

The Brightcove Player Controls integration is developed with:
* Gradle 2.2.1

The Brightcove Player Controls integration has been tested using:
* Android Studio 1.1.0
* Android Studio 1.2 Beta

Eclipse is not officially supported.

## Installation

The Brightcove Player Controls are available with the Android Native Player version 4.3.6 and higher.  Simply ensure that a top level Gradle properties file is provided that defines *anpVersion* with the value "4.4+", for example:

    # gradle.properties

    # Use this property to select the most recent Brightcove Android
    # Native Player version.
    anpVersion=4.4+

## Quick Start

## Known Issues

### Orientation Switching

#### SeekBar
* There is currently a known bug in the reported progress after switching orientation.  After the orientation switch the seek bar shows progress as completed.

### End Time Text View prior to starting the video
* There is currently a known bug where the duration text view does not show the video length prior to starting the video.  Once the video is started, the text view value is updated correctly.

## Reporting Bugs

To report any bugs or otherwise odd behavior found within the Brightcove Player Controls integration, please submit the following
to the [Brightcove Native Player SDKs Google Group](https://groups.google.com/forum/#!forum/brightcove-native-player-sdks):

1. Please preface your post with an [Brightcove Player Controls] tag.
2. Include steps to reproduce the problem.
3. Include the version of Android and manufacturer of the device(s) you are testing on.
4. Include a full logcat of the execution of your sample application demonstrating the problem.
5. Include any screenshots that help to demonstrate the problem.
