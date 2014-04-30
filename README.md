android-player-samples
======================

Still under development and is experimental  - please use at your own risk.

======================

Provides sample apps for the Brightcove Player SDK and Plugins for Android.

The Android sample app projects in this repository can be inserted directly into Android Studio and subsequently executed or simulated.  See below for detailed installation steps.

This version of the sample apps supports the latest Brightcove SDK and plugins.  The following sample apps are included:

* [Ad Rules Google IMA Sample App](http://docs.brightcove.com/en/video-cloud/brightcove-player-sdk-for-android/index.html "AdRulesIMASampleApp"): This app shows how to setup to use the Google IMA Plugin to play ads via Ad Rules. This version has been tested and works with v3 of the IMA SDK.

* [Basic Akamai Identity Services Sample App](http://docs.brightcove.com/en/video-cloud/brightcove-player-sdk-for-android/index.html "BasicAISWebViewSampleApp"): This app shows how to configure an app to use the Brightcove native Android player with Akamai Identity Services.

* [Basic AdobePass Sample App](http://docs.brightcove.com/en/video-cloud/brightcove-player-sdk-for-android/index.html "BasicAdobePassWebViewSampleApp"): This app shows how to configure an app to use the Brightcove native Android player with AdobePass.

* [Basic Bundled Video Sample App](http://docs.brightcove.com/en/video-cloud/brightcove-player-sdk-for-android/index.html "BasicWidevineSampleApp"): This app shows how to play a video that is stored on the device for offline viewing.

* [Basic Cast Sample App](http://docs.brightcove.com/en/video-cloud/brightcove-player-sdk-for-android/index.html "BasicCastSampleApp"): This add Google Chromecast with the Brightcove native Android player.

* [Basic FreeWheel Sample App](http://docs.brightcove.com/en/video-cloud/brightcove-player-sdk-for-android/index.html "BasicFreeWheelSampleApp"): This app shows how to configure an app to use the Brightcove native Android player FreeWheel Plugin to play a video.

Note that in order to enable this sample app, you must independently obtain and install the non-free file **AdManager.jar** into the top-level directory **libs/**.  The *Basic FreeWheel Sample App* was tested with version 5.7.2.  Contact [FreeWheel](http://www.freewheel.tv/about/contact "FreeWheel") directly for more information on how to obtain their products.

* [Basic Google IMA Sample App](http://docs.brightcove.com/en/video-cloud/brightcove-player-sdk-for-android/index.html "BasicIMASampleApp"): This app shows how to setup to use the Google IMA Plugin to play ads before, during and after a video. This version has been tested and works with v3 of the IMA SDK.

* [Basic Google IMA Widevine Sample App](http://docs.brightcove.com/en/video-cloud/brightcove-player-sdk-for-android/index.html "BasicIMAWidevineSampleApp"): This app shows how to setup to use the Google IMA Plugin to play ads before, during and after a Widevine video. This version has been tested and works with v3 of the IMA SDK.

* [Basic Omniture Sample App](http://docs.brightcove.com/en/video-cloud/brightcove-player-sdk-for-android/index.html "BasicOmnitureSampleApp"): This app shows how to configure an app to use the Brightcove native Android player Omniture Plugin to play a video.

Note that in order to enable this sample app, you must independently obtain and install the file **adobe-adms.jar** into the top-level directory **libs/**.  Version 3.2.2 of the Adobe provided, non-free, jar file was used to test this sample app.

* [Basic Widevine Sample App](http://docs.brightcove.com/en/video-cloud/brightcove-player-sdk-for-android/index.html "BasicWidevineSampleApp"): This app shows how to configure an app to use the Brightcove native Android player Widevine Plugin to play a video.

* [HLS Player ID3 Sample App](http://docs.brightcove.com/en/video-cloud/brightcove-player-sdk-for-android/index.html "HLSPlayerID3SampleApp"): This app shows how to use ID3 tags with the HLS player.

* [HLS Player Sample App](http://docs.brightcove.com/en/video-cloud/brightcove-player-sdk-for-android/index.html "HLSPlayerSampleApp"): This app shows how to setup the HLS player to play.

* [WebVTT Sample App](http://docs.brightcove.com/en/video-cloud/brightcove-player-sdk-for-android/index.html "WebVTTSampleApp"): This app shows how to implement closed captioning using WebVTT files with Android 4.4.


By default, the sample apps will build with the most recent Brightcove Android Native Player version at build time.  To override this behavior with a specific version, create a file named **.gradle/gradle.properties** in your home directory and set the value of the property *anpVersion* to the desired version.  An invalid version will cause no sample app projects to be configured.

To install the sample apps into Android Studio follow these steps:

1. Obtain and install the latest version of [Android Studio](http://developer.android.com/sdk/installing/studio.html) using the provided on-line instructions,
1. Configure Android Studio for Android versions from Android 10 to Android 19 (see Android Studio help for details),
1. Invoke the new project wizard using the File menu if it is not presented by default,

From Android Studio using VCS installation:

1. Select the *Checkout From Version Control* option in the new project dialog,
1. Pick the GITHUB menu entry
1. Use **git@github.com:BrightcoveOS/android-player-samples.git** for the URL
1. Click on SUBMIT and use default values on subsequent screens.

From Android Studio using Import

1. Clone this repo to your file system.
1. Select the *Import Project...* option in the new project dialog,
1. Using the file selector dialog, select the top level **build.gradle** file from the cloned repo on your system,
1. Click on OK and use default values on subsequent sceens.

Android Studio will now download/import and build the sample app.  At this point the samples will be in your instance of Android Studio where you can observe the snippets in action on a physical or virtual device, walk through the code using the Android Studio debugger, search on keywords or just browse Android source code.

1. To run on an Android 4.2.2 or later device, plug the device into the computer and ensure that USB debugging is enabled.  Android Studio will recognize the device as long as the device identifies as running a version between Android 10 and Android 19 otherwise Android Studio will run the sample app in the simulator.  Using the simulator is not recommended as video support is buggy.  This will be resolved as Android Studio and the Android SDK mature.
1. Select a sample app from the *Run/Debug Configuration* selector and click on the run (green button) icon to start the sample.

Enjoy!

The Brightcove Player Android Team
