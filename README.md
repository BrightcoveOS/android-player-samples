# Brightcove Player SDK for Android Samples

Sample apps for the [Brightcove Player SDK for Android](https://support.brightcove.com/native-player-sdks) and its plugins. This is the reference repository for building with the SDK: each capability is demonstrated by a small, focused app, and every capability ships a **Java** and a **Kotlin** variant kept at parity.

## Prerequisites

- The latest [Android Studio](https://developer.android.com/studio).
- **JDK 17** (required since SDK 8.3.0).
- Android **5.0 (API 21)** or later on the device/emulator; the apps compile and target **API 36**.
- Builds with the bundled **Gradle 8.13** wrapper.
- The SDK version is pinned by `anpVersion` in [`gradle.properties`](gradle.properties) (currently `10.4.25`). To build against a different version, set `anpVersion` in `~/.gradle/gradle.properties`. To develop against a local SDK checkout, set `anpVersion=LOCAL` (see [`settings.gradle`](settings.gradle)).
- A few samples need artifacts Brightcove does not distribute — the FreeWheel `AdManager.aar` and the Pulse `Pulse.aar`. See the [FreeWheel](FreeWheel/) and [Pulse](Pulse/) READMEs.

## Repository layout

Samples are grouped by **capability**. Each top-level folder collects one capability's samples and has its own README covering shared setup; inside, each sample ships as `…SampleApp-java` and `…SampleApp-kotlin`.

```
Player/  PlayerUI/  IMA/  DAI/  SSAI/  FreeWheel/  Pulse/  DRM/  Cast/  Offline/
```

## Capabilities

- **Playback** — [`Player/`](Player/): basic playback, video list / playlist, live / DVR, 360 video, audio-only playback, Picture-in-Picture, thumbnail scrubbing, plus the Android-specific TextureView, AppCompat entry points, and bumper samples.
- **UI customization** — [`PlayerUI/`](PlayerUI/): custom controls — customized and styled controls, seek-bar colors, and a rewind button.
- **Advertising** — [`IMA/`](IMA/) · [`DAI/`](DAI/) · [`SSAI/`](SSAI/) · [`FreeWheel/`](FreeWheel/) · [`Pulse/`](Pulse/): Google IMA (client-side), Google DAI, Brightcove server-side ad insertion (incl. PAL), FreeWheel, and Pulse.
- **DRM & offline** — [`DRM/`](DRM/) · [`Offline/`](Offline/): Widevine Modular playback and offline (downloaded) playback.
- **Casting** — [`Cast/`](Cast/): Google Cast with the Brightcove Cast Receiver.

## Coverage matrix

Every capability ships a Java and a Kotlin sample.

| Capability | Sample(s) |
|---|---|
| Basic playback | [`Player/BasicSampleApp-java`](Player/BasicSampleApp-java/) · [`Player/BasicSampleApp-kotlin`](Player/BasicSampleApp-kotlin/) |
| Video list / playlist | [`Player/VideoListSampleApp-java`](Player/VideoListSampleApp-java/) · [`Player/VideoListSampleApp-kotlin`](Player/VideoListSampleApp-kotlin/) |
| Live / DVR | [`Player/LiveSampleApp-java`](Player/LiveSampleApp-java/) · [`Player/LiveSampleApp-kotlin`](Player/LiveSampleApp-kotlin/) |
| 360 video | [`Player/360VideoSampleApp-java`](Player/360VideoSampleApp-java/) · [`Player/360VideoSampleApp-kotlin`](Player/360VideoSampleApp-kotlin/) |
| Audio-only playback | [`Player/AudioOnlySampleApp-java`](Player/AudioOnlySampleApp-java/) · [`Player/AudioOnlySampleApp-kotlin`](Player/AudioOnlySampleApp-kotlin/) |
| Picture-in-Picture | [`Player/PictureInPictureSampleApp-java`](Player/PictureInPictureSampleApp-java/) · [`Player/PictureInPictureSampleApp-kotlin`](Player/PictureInPictureSampleApp-kotlin/) |
| Thumbnail scrubbing | [`Player/ThumbnailScrubberSampleApp-java`](Player/ThumbnailScrubberSampleApp-java/) · [`Player/ThumbnailScrubberSampleApp-kotlin`](Player/ThumbnailScrubberSampleApp-kotlin/) |
| Custom controls | [`PlayerUI/`](PlayerUI/) — 4 samples |
| IMA ads | [`IMA/`](IMA/) — 4 samples |
| DAI | [`DAI/BasicDAISampleApp-java`](DAI/BasicDAISampleApp-java/) · [`DAI/BasicDAISampleApp-kotlin`](DAI/BasicDAISampleApp-kotlin/) |
| SSAI | [`SSAI/`](SSAI/) — 2 samples |
| FreeWheel | [`FreeWheel/`](FreeWheel/) — 2 samples |
| Pulse | [`Pulse/PulseSampleApp-java`](Pulse/PulseSampleApp-java/) · [`Pulse/PulseSampleApp-kotlin`](Pulse/PulseSampleApp-kotlin/) |
| DRM (Widevine) | [`DRM/WidevineModularSampleApp-java`](DRM/WidevineModularSampleApp-java/) · [`DRM/WidevineModularSampleApp-kotlin`](DRM/WidevineModularSampleApp-kotlin/) |
| Casting | [`Cast/BasicCastBrightcoveReceiverSampleApp-java`](Cast/BasicCastBrightcoveReceiverSampleApp-java/) · [`Cast/BasicCastBrightcoveReceiverSampleApp-kotlin`](Cast/BasicCastBrightcoveReceiverSampleApp-kotlin/) |
| Offline playback | [`Offline/OfflinePlaybackSampleApp-java`](Offline/OfflinePlaybackSampleApp-java/) · [`Offline/OfflinePlaybackSampleApp-kotlin`](Offline/OfflinePlaybackSampleApp-kotlin/) |
| Bumper | [`Player/BumperSampleApp-java`](Player/BumperSampleApp-java/) · [`Player/BumperSampleApp-kotlin`](Player/BumperSampleApp-kotlin/) |
| TextureView | [`Player/TextureViewSampleApp-java`](Player/TextureViewSampleApp-java/) · [`Player/TextureViewSampleApp-kotlin`](Player/TextureViewSampleApp-kotlin/) |
| AppCompat entry points | [`Player/AppCompatSampleApp-java`](Player/AppCompatSampleApp-java/) · [`Player/AppCompatSampleApp-kotlin`](Player/AppCompatSampleApp-kotlin/) |

## Building

Open the project in Android Studio one of these ways:

- **Get from VCS** (Welcome screen) or **File > New > Project from Version Control…**, select **Git**, and enter `https://github.com/BrightcoveOS/android-player-samples.git`; or
- clone the repository and choose **File > Open…** on its root folder.

Android Studio downloads dependencies and builds every sample. To build from the command line:

```
./gradlew assembleDebug                                  # all samples
./gradlew :Player:BasicSampleApp-kotlin:assembleDebug    # one sample
./gradlew --stop                                         # stop the Gradle daemon afterwards
```

## Running a sample

Plug in an Android 5.0+ device with USB debugging enabled (or start an emulator), pick the sample from the **Run/Debug Configuration** selector, and run it.

## Support

Use the [Support Portal](https://supportportal.brightcove.com/s/login/) or contact your Account Manager. To hear about new SDK releases, subscribe to the Brightcove Native Player SDKs [Google Group](https://groups.google.com/g/brightcove-native-player-sdks).
