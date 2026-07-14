# DRM

These samples demonstrate playback of Widevine Modular DRM-protected content. Widevine support is built into the Brightcove SDK, so no additional plugin is required.

## Requirements

- **Platform:** Android phone and tablet.
- **Minimum OS:** Android 5.0 (API 21).
- **Toolchain:** Android Studio, JDK 17, Gradle 8.13.
- **Extra SDKs:** none — Widevine is handled by the SDK.

## Setup

The sample plays a Widevine-protected asset from the shared Brightcove demo account (`5420904993001`); license acquisition is handled by the SDK.

## Samples

| Sample | Languages | What it demonstrates |
|---|---|---|
| `WidevineModularSampleApp` | [Java](WidevineModularSampleApp-java/) · [Kotlin](WidevineModularSampleApp-kotlin/) | Widevine Modular protected playback. |
