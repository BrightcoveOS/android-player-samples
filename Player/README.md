# Playback

These samples show how to play Brightcove Video Cloud content with the Brightcove Player SDK — from the minimal setup up through live, 360, audio-only, Picture-in-Picture, and thumbnail scrubbing, plus a few Android-specific playback samples.

## Requirements

- **Platform:** Android phone and tablet (Picture-in-Picture is phone/tablet only).
- **Minimum OS:** Android 5.0 (API 21). Picture-in-Picture requires Android 8.0 (API 26).
- **Toolchain:** Android Studio, JDK 17, Gradle 8.13.
- **Extra SDKs:** none for most samples. Audio-only adds the playback-notification plugin and thumbnail scrubbing adds the thumbnail plugin; both are wired automatically by the build.

## Setup

Most samples play from the shared Brightcove demo account (`5420904993001`); the 360 video and audio-only samples use their own demo accounts. The **live** sample ships with a placeholder URL — live streams are ephemeral, so supply your own Live/DVR HLS URL and publisher id in the source before running it.

## Samples

| Sample | Languages | What it demonstrates |
|---|---|---|
| `BasicSampleApp` | [Java](BasicSampleApp-java/) · [Kotlin](BasicSampleApp-kotlin/) | Minimal playback — a `BrightcoveExoPlayerVideoView` playing a catalog video. |
| `VideoListSampleApp` | [Java](VideoListSampleApp-java/) · [Kotlin](VideoListSampleApp-kotlin/) | Multiple player instances in a scrolling `RecyclerView` (video list / playlist). |
| `LiveSampleApp` | [Java](LiveSampleApp-java/) · [Kotlin](LiveSampleApp-kotlin/) | HLS Live and Live DVR playback. Requires a developer-supplied Live/DVR URL. |
| `360VideoSampleApp` | [Java](360VideoSampleApp-java/) · [Kotlin](360VideoSampleApp-kotlin/) | 360° video with touch and gyroscope navigation. |
| `AudioOnlySampleApp` | [Java](AudioOnlySampleApp-java/) · [Kotlin](AudioOnlySampleApp-kotlin/) | Audio-only assets with a background playback notification. |
| `PictureInPictureSampleApp` | [Java](PictureInPictureSampleApp-java/) · [Kotlin](PictureInPictureSampleApp-kotlin/) | Picture-in-Picture mode (Android 8.0+, phone/tablet). |
| `ThumbnailScrubberSampleApp` | [Java](ThumbnailScrubberSampleApp-java/) · [Kotlin](ThumbnailScrubberSampleApp-kotlin/) | Thumbnail previews while scrubbing the seek bar. |
| `TextureViewSampleApp` | [Java](TextureViewSampleApp-java/) · [Kotlin](TextureViewSampleApp-kotlin/) | Rendering into a `TextureView` instead of the default `SurfaceView`. Android-specific. |
| `AppCompatSampleApp` | [Java](AppCompatSampleApp-java/) · [Kotlin](AppCompatSampleApp-kotlin/) | The AppCompat plugin, shown through both an Activity and a Fragment entry point. Android-specific. |
| `BumperSampleApp` | [Java](BumperSampleApp-java/) · [Kotlin](BumperSampleApp-kotlin/) | Playing a bumper video ahead of the main content. Android-specific. |
