# UI Customization

These samples show how to customize the Brightcove Player Controls: swap Font Awesome icons, restyle the controller to mirror the Brightcove web player, recolor the seek bar, and add controls such as a rewind button.

## Requirements

- **Platform:** Android phone and tablet.
- **Minimum OS:** Android 5.0 (API 21).
- **Toolchain:** Android Studio, JDK 17, Gradle 8.13.
- **Extra SDKs:** none — the Brightcove Player Controls are part of the core SDK.

## Setup

The samples play from the shared Brightcove demo account (`5420904993001`). Controls are defined in layout XML loaded by resource id, so these samples customize the `BrightcoveMediaController` through layouts and code rather than data binding.

## Samples

| Sample | Languages | What it demonstrates |
|---|---|---|
| `CustomizedControlsSampleApp` | [Java](CustomizedControlsSampleApp-java/) · [Kotlin](CustomizedControlsSampleApp-kotlin/) | Customizing the media controller layout and Font Awesome icons. |
| `StyledControlsSampleApp` | [Java](StyledControlsSampleApp-java/) · [Kotlin](StyledControlsSampleApp-kotlin/) | Restyling the controls to mirror the Brightcove web player. |
| `SeekBarColorsSampleApp` | [Java](SeekBarColorsSampleApp-java/) · [Kotlin](SeekBarColorsSampleApp-kotlin/) | Changing the seek-bar colors. |
| `RewindSampleApp` | [Java](RewindSampleApp-java/) · [Kotlin](RewindSampleApp-kotlin/) | Adding a rewind button; the strings file documents the Font Awesome glyph options. |
