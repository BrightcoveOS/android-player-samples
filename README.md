android-player-samples
======================

Provides sample apps for the Brightcove Player SDK and Plugins for Android.

Learn more about the [Brightcove Native Player SDKs](https://support.brightcove.com/native-player-sdks).

The Android sample app projects in this repository can be inserted directly into Android Studio and subsequently executed or simulated.
This version of the sample apps supports the latest Brightcove SDK and plugins.  The following sample apps are included:

## Brightcove ExoPlayer 2 sample apps:

### Important Notes about the ExoPlayer 2 sample apps
1. The ExoPlayer sample apps on this branch of the repository (ExoPlayer2) only support Google ExoPlayer 2. If you want to run ExoPlayer 1 versions of the ExoPlayer sample apps, please `git checkout master` and run them on the master branch.
2. The Brightcove ExoPlayer 2 sample apps do not currently support Android TV. Support for this platform will be delivered in a later release.
3. The Brightcove ExoPlayer 2 sample apps do not currently support Amazon Fire TV or Fire Stick. Support for this platform will be delivered in a later release.
4. The supported features for this Beta 1 release include:
   * VOD video formats: Mpeg-DASH, HLS, HLSe, MP4
   * WebVTT Closed Captions
   * Multiple Audio Track videos
   * Client-side advertising with Google IMA and FreeWheel
5. Features not supported in Beta 1, and planned for an upcoming beta release, include:
   * Live video, and Live video with DVR
   * Server-side Ad Insertion
   * DRM with Widevine Modular
   * Offline playback of clear and DRM-protected content
### Only the apps listed below are supported in the Beta 1 release:

* [Ad Rules Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/ExoPlayer2/brightcove-exoplayer/AdRulesIMASampleApp): This app shows how to use the Brightcove ExoPlayer with Google IMA ads.

* [AppCompat Activity Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/ExoPlayer2/brightcove-exoplayer/AppCompatActivitySampleApp): This app shows how to setup the Brightcove ExoPlayer with the AppCompat plugin and an Activity.

* [AppCompat Fragment Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/ExoPlayer2/brightcove-exoplayer/AppCompatFragmentSampleApp): This app shows how to setup the Brightcove ExoPlayer with the AppCompat plugin and a Fragment.

* [Basic Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/ExoPlayer2/brightcove-exoplayer/BasicSampleApp): This app shows how to setup the Brightcove ExoPlayer to play.

* [FreeWheel Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/ExoPlayer2/brightcove-exoplayer/FreeWheelSampleApp): This app shows how to use the Brightcove ExoPlayer with FreeWheel ads.

As noted above, you must independently obtain the non-free FreeWheel **AdManager.jar** library archive and install it into the directory **libs/** which is typically located at the root of your /home/ directory. The Brightcove Native SDK for Android, and the FreeWheel sample apps here, have been tested with FreeWheel AdManager library version 6.5.0. Please note that Brightcove does not distribute the FreeWheel libraries. Contact [FreeWheel](http://freewheel.tv/about/#contact-us) directly for more information on how to obtain their products.

* [TextureView Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/ExoPlayer2/brightcove-exoplayer/TextureViewSampleApp): This app shows how to use the Brightcove ExoPlayer with TextureView.

* [VideoList Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/ExoPlayer2/brightcove-exoplayer/VideoListSampleApp): This app shows how to use the Brightcove ExoPlayer with a RecyclerView.

## Brightcove Android SDK Samples Using the Android MediaPlayer

* [Ad Rules Google IMA Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/ExoPlayer2/AdRulesIMASampleApp): This app shows how to setup to use the Google IMA Plugin to play ads via Ad Rules. This version has been tested and works with v3.1.3 of the IMA SDK.

* [Basic Bundled Video Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/ExoPlayer2/BasicBundledVideoSampleApp): This app shows how to play a video that is stored on the device for offline viewing.

* [Basic Cast Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/ExoPlayer2/BasicCastSampleApp): This add Google Chromecast with the Brightcove native Android player. Note that this app requires installation of the Google Play and Google Repository plugins into the Android Studio. To install these plugins, open Android Studio, and then open the Tools menu. From here, select Android, then SDK Manager. When the SDK Manager opens, scroll to the bottom and select Google Play Services and Google Repository under the Extras menu. Click Install Packages, accept the license agreement, and close the SDK Manager when installation completes.

* [Basic FreeWheel Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/ExoPlayer2/BasicFreeWheelSampleApp): This app shows how to configure an app to use the Brightcove native Android player FreeWheel Plugin to play a video.

Note that in order to enable the FreeWheel sample apps, you must independently obtain the non-free FreeWheel **AdManager.jar** library archive and install it into the directory **libs/** which is typically located at the root of your /home/ directory. The Brightcove Native SDK for Android, and the FreeWheel sample apps here, have been tested with FreeWheel AdManager library version 6.5.0. Please note that Brightcove does not distribute the FreeWheel libraries. Contact [FreeWheel](http://freewheel.tv/about/#contact-us) directly for more information on how to obtain their products.

* [Basic Google IMA Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/ExoPlayer2/BasicIMASampleApp): This app shows how to setup to use the Google IMA Plugin to play ads before, during and after a video. This version has been tested and works with v3.1.3 of the IMA SDK.

* [Basic Omniture Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/ExoPlayer2/BasicOmnitureSampleApp): This app shows how to configure an app to use the Brightcove native Android player Omniture Plugin to play a video.

Note that in order to enable this sample app, you must independently obtain and install the file **adobeMobileLibrary.jar** into the top-level directory **libs/**.  Version 4.11.0 of the Adobe provided, non-free, jar file was used to test this sample app.

* [WebVTT Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/ExoPlayer2/WebVTTSampleApp): This app shows how to implement closed captioning using WebVTT files with Android 4.4.

To install the sample apps into Android Studio follow these steps:

1. Obtain and install the latest version of [Android Studio](http://developer.android.com/sdk/installing/studio.html) using the provided on-line instructions,
1. Configure Android Studio for Android versions from Android 10 to Android 19 (see Android Studio help for details),
1. Invoke the new project wizard using the File menu if it is not presented by default,

From Android Studio using VCS installation:

1. Select the *Checkout From Version Control* option in the new project dialog,
1. Pick the GITHUB menu entry
1. Use **https://github.com/BrightcoveOS/android-player-samples.git** for the URL
1. Click on SUBMIT and use default values on subsequent screens.

From Android Studio using Import

1. Clone this repo to your file system.
1. Select the *Import Project...* option in the new project dialog,
1. Using the file selector dialog, select the top level **build.gradle** file from the cloned repo on your system,
1. Click on OK and use default values on subsequent sceens.

Android Studio will now download/import and build the sample app.  At this point the samples will be in your instance of Android Studio where you can observe the snippets in action on a physical or virtual device, walk through the code using the Android Studio debugger, search on keywords or just browse Android source code.

## Running a sample app
1. To run a sample app on an Android 4.4.4 or later device, plug the device into the computer and ensure that USB debugging is enabled.  Android Studio will recognize the device as long as the device identifies as running a version between Android 10 and Android 19 otherwise Android Studio will run the sample app in the simulator.  Using the simulator is not recommended as video support is buggy.  This will be resolved as Android Studio and the Android SDK mature.
1. Select a sample app from the *Run/Debug Configuration* selector and click on the run (green button) icon to start the sample.

Enjoy!

The Brightcove Player Android Team

