android-player-samples
======================

Provides sample apps for the Brightcove Player SDK and Plugins for Android.

Learn more about the [Brightcove Native Player SDKs](https://support.brightcove.com/native-player-sdks).

The Android sample app projects in this repository can be inserted directly into Android Studio and subsequently executed or simulated.
This version of the sample apps supports the latest Brightcove SDK and plugins.  The following sample apps are included:

## Brightcove ExoPlayer sample apps:

### An important note 
Starting from the SDK v8.3.0, the sample apps require Java 17.

The Issues tab is now closed. To report any problem or provide feedback, please refer to the Support section below.  

### Brightcove Java sample apps using ExoPlayer

* [360 Video Sample app](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/360VideoSampleApp/): This app shows how to use the Brightcove ExoPlayer to play 360 videos.

* [Ad Rules Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/AdRulesIMASampleApp): This app shows how to use the Brightcove ExoPlayer with Google IMA ads.

* [Ad Rules Widevine Modular Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/AdRulesIMAWidevineModularSampleApp): This app shows how to use the Brightcove ExoPlayer with Google IMA ads and Widevine Modular content.

* [AppCompat Activity Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/AppCompatActivitySampleApp): This app shows how to setup the Brightcove ExoPlayer with the AppCompat plugin and an Activity.

* [AppCompat Fragment Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/AppCompatFragmentSampleApp): This app shows how to setup the Brightcove ExoPlayer with the AppCompat plugin and a Fragment.

* [Audio Only Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/AudioOnlySampleApp): This app shows how to setup the Brightcove ExoPlayer with audio-only assets.

* [Basic Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/BasicSampleApp): This app shows how to setup the Brightcove ExoPlayer to play.

* [Basic Cast Sample App with Brightcove Cast Receiver](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/BasicCastBrightcoveReceiverSampleApp): This adds Google Chromecast support to the Brightcove Native Player SDK for Android, and demonstrates integration with the Brightcove Cast Receiver app.

* [BasicIMAVAST Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/BasicIMAVASTSampleApp): This app shows how to use the Brightcove ExoPlayer with Google IMA ads.

* [BasicDAI Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/BasicDAISampleApp): This app shows how to use the Brightcove ExoPlayer with Google DAI ads.

* [Bumper Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/BumperSampleApp): This app shows how to setup the Brightcove ExoPlayer to play using a bumper video.

* [FreeWheel Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/FreeWheelSampleApp): This app shows how to use the Brightcove ExoPlayer with FreeWheel ads.

* [FreeWheel Widevine Modular Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/FreeWheelWidevineModularSampleApp): This app shows how to use the Brightcove ExoPlayer with FreeWheel ads and Widevine Modular content.

Note that in order to enable the FreeWheel sample apps, you must independently obtain the non-free FreeWheel **AdManager.aar** library archive and install it into the directory **libs/** which is typically located at the root of your /home/ directory. The Brightcove Native SDK for Android and the FreeWheel sample apps require the FreeWheel AdManager library version 6.28.0. Please note that Brightcove does not distribute the FreeWheel libraries. Contact [FreeWheel](http://freewheel.tv/about/#contact-us) directly for more information on how to obtain their products.

* [Basic SSAI Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/BasicSsaiSampleApp): This app shows how to configure an app to use the Brightcove Native Player for Android SSAI Plugin to play a video.

* [Basic SSAI PAL Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/BasicSSAIPALSampleApp): This app shows how to configure an app to use the Brightcove Native Player for Android SSAI Plugin to play a video and the PAL SDK from Google for Ad tracking.

* [Live Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/LiveSampleApp): This app shows how to use the Brightcove ExoPlayer with HLS Live and Live DVR content. Please note that a Live/DVR URL is not supplied with this sample app, and must be supplied by the developer.

* [Offline Playback Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/OfflinePlaybackSampleApp): This app demonstrates the Offline Playback feature.

* [Picture In Picture Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/PictureInPictureSampleApp): This app demonstrates the Picture-in-Picture feature. Please note that Picture-in-Picture is supported only with Android 8.0 and above, and only on phone and tablet devices.

* [Pulse Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/PulseSampleApp): This app shows how to setup the Brightcove ExoPlayer with Pulse ads.

* [TextureView Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/TextureViewSampleApp): This app shows how to use the Brightcove ExoPlayer with TextureView.

* [ThumbnailScrubber Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/ThumbnailScrubberSampleApp): This app demonstrates how to use the Brightcove ExoPlayer with thumbnails.

* [VideoListAdRulesIMA Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/VideoListAdRulesIMASampleApp): This app shows how to use multiple instances of the Brightcove SDK in one screen with Google IMA ads.

* [VideoList Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/VideoListSampleApp): This app shows how to use multiple instances of the Brightcove SDK in one screen.

* [Widevine Modular Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer/WidevineModularSampleApp): This app shows how to setup the Brightcove ExoPlayer with Widevine Modular content.

### Brightcove Kotlin sample apps using ExoPlayer

* [Ad Rules Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/AdRulesIMASampleApp): This app shows how to use the Brightcove ExoPlayer with Google IMA ads.

* [Ad Rules Widevine Modular Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/AdRulesIMAWidevineModularSampleApp): This app shows how to use the Brightcove ExoPlayer with Google IMA ads and Widevine Modular content.

* [AppCompat Activity Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/AppCompatActivitySampleApp): This app shows how to setup the Brightcove ExoPlayer with the AppCompat plugin and an Activity.

* [AppCompat Fragment Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/AppCompatFragmentSampleApp): This app shows how to setup the Brightcove ExoPlayer with the AppCompat plugin and a Fragment.

* [Audio Only Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/AudioOnlySampleApp): This app shows how to setup the Brightcove ExoPlayer with audio-only assets.

* [Basic Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/BasicSampleApp): This app shows how to setup the Brightcove ExoPlayer to play.

* [Basic Cast Sample App with Brightcove Cast Receiver](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/BasicCastBrightcoveReceiverSampleApp): This adds Google Chromecast support to the Brightcove Native Player SDK for Android, and demonstrates integration with the Brightcove Cast Receiver app.

* [BasicIMAVAST Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/BasicIMAVASTSampleApp): This app shows how to use the Brightcove ExoPlayer with Google IMA ads.

* [BasicDAI Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/BasicDAISampleApp): This app shows how to use the Brightcove ExoPlayer with Google DAI ads.

* [Bumper Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/BumperSampleApp): This app shows how to setup the Brightcove ExoPlayer to play using a bumper video.

* [FreeWheel Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/FreeWheelSampleApp): This app shows how to use the Brightcove ExoPlayer with FreeWheel ads.

* [FreeWheel Widevine Modular Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/FreeWheelWidevineModularSampleApp): This app shows how to use the Brightcove ExoPlayer with FreeWheel ads and Widevine Modular content.

Note that in order to enable the FreeWheel sample apps, you must independently obtain the non-free FreeWheel **AdManager.aar** library archive and install it into the directory **libs/** which is typically located at the root of your /home/ directory. The Brightcove Native SDK for Android and the FreeWheel sample apps require the FreeWheel AdManager library version 6.28.0. Please note that Brightcove does not distribute the FreeWheel libraries. Contact [FreeWheel](http://freewheel.tv/about/#contact-us) directly for more information on how to obtain their products.

* [Basic SSAI Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/BasicSsaiSampleApp): This app shows how to configure an app to use the Brightcove Native Player for Android SSAI Plugin to play a video.

* [Basic SSAI PAL Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/BasicSsaiPALSampleApp): This app shows how to configure an app to use the Brightcove Native Player for Android SSAI Plugin to play a video and the PAL SDK from Google for Ad tracking.

* [Live Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/LiveSampleApp): This app shows how to use the Brightcove ExoPlayer with HLS Live and Live DVR content. Please note that a Live/DVR URL is not supplied with this sample app, and must be supplied by the developer.

* [Offline Playback Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/OfflinePlaybackSampleApp): This app demonstrates the Offline Playback feature.

* [Picture In Picture Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/PictureInPictureSampleApp): This app demonstrates the Picture-in-Picture feature. Please note that Picture-in-Picture is supported only with Android 8.0 and above, and only on phone and tablet devices.

* [TextureView Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/TextureViewSampleApp): This app shows how to use the Brightcove ExoPlayer with TextureView.

* [ThumbnailScrubber Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/ThumbnailScrubberSampleApp): This app demonstrates how to use the Brightcove ExoPlayer with thumbnails.

* [VideoListAdRulesIMA Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/VideoListAdRulesIMASampleApp): This app shows how to use multiple instances of the Brightcove SDK in one screen with Google IMA ads.

* [VideoList Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/VideoListSampleApp): This app shows how to use multiple instances of the Brightcove SDK in one screen. 

* [Widevine Modular Sample App](https://github.com/BrightcoveOS/android-player-samples/tree/master/brightcove-exoplayer-kotlin/WidevineModularSampleApp): This app shows how to setup the Brightcove ExoPlayer with Widevine Modular content.


## Installing the sample apps
Currently, all of the sample app projects must be installed together as a bundle. Individual sample app projects also have specific dependencies. For those specific dependencies, see the individual project descriptions above for details.

To build the sample apps, install the latest [Android Studio](https://developer.android.com/studio). Then open the project one of these ways:

Clone directly from Android Studio:

1. On the Welcome screen, choose **Get from VCS** (or **File > New > Project from Version Control…**).
1. Select **Git** and enter the URL **https://github.com/BrightcoveOS/android-player-samples.git**.
1. Click **Clone** and accept the defaults on the following screens.

Open a local clone:

1. Clone this repository to your file system.
1. In Android Studio, choose **File > Open…** and select the cloned repository's root folder.
1. Click **OK** and accept the defaults on the following screens.

Android Studio will now download, import and build all of the sample apps.  At this point the samples will be in your instance of Android Studio where you can observe the snippets in action on a physical or virtual device, walk through the code using the Android Studio debugger, search on keywords or just browse Android source code.

## Overriding the default build behavior
By default, the sample apps will build with the most recent Brightcove Android Native Player version at build time. To override this behavior with a specific version, create a file named **.gradle/gradle.properties** in your home directory and set the value of the property *anpVersion* to the desired version.  An invalid version will cause no sample app projects to be configured.

## Building with Gradle on the command line
These sample apps now build with the latest released version of Gradle, 7.4.1. Please note that when building these apps from the command line, that the Gradle daemon is started, and will continue to run after the build completes.

To stop the daemon after the build completes, run the following:
`./gradlew --stop`

## Running a sample app
1. To run a sample app on an Android 5.0 or later device, plug the device into the computer and ensure that USB debugging is enabled.
1. Select a sample app from the *Run/Debug Configuration* selector and click on the run (green button) icon to start the sample.

## Known Issues and Limitations

* **NextGen Live SSAI (Live 2.0)** — Full SSAI support including ad countdown overlay, click-through, and tracking beacons since SDK **10.4.20**.

## Support
If you have questions, need help or want to provide feedback, please use the [Support Portal](https://supportportal.brightcove.com/s/login/) or contact your Account Manager.  To receive notification of new SDK software releases, subscribe to the Brightcove Native Player SDKs [Google Group](https://groups.google.com/g/brightcove-native-player-sdks).

Enjoy!

The Brightcove Player Android Team
