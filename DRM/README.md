# DRM

These samples demonstrate playback of Widevine Modular DRM-protected content. Widevine support is built into the Brightcove SDK, so no additional plugin is required.

## Requirements

- **Platform:** Android phone and tablet.
- **Minimum OS:** Android 5.0 (API 21).
- **Toolchain:** Android Studio, JDK 17, Gradle 8.13.
- **Extra SDKs:** none — Widevine is handled by the SDK.

## Setup

Each sample plays a Widevine-protected asset and license acquisition is handled by the SDK.
`WidevineModularSampleApp` uses the shared Brightcove demo account (`5420904993001`).
`HdcpFallbackSampleApp` needs an account enabled for Fallback HDCP, so that the renditions
carry a separate DRM key per tier, and points at one (`6415550737001`).

## Samples

| Sample | Languages | What it demonstrates |
|---|---|---|
| `WidevineModularSampleApp` | [Java](WidevineModularSampleApp-java/) · [Kotlin](WidevineModularSampleApp-kotlin/) | Widevine Modular protected playback. |
| `HdcpFallbackSampleApp` | [Java](HdcpFallbackSampleApp-java/) · [Kotlin](HdcpFallbackSampleApp-kotlin/) | Fallback HDCP: restricting rendition selection to SD while the video output is not HDCP protected. |
