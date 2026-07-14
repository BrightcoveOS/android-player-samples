# SSAI (Server-Side Ad Insertion)

These samples demonstrate Brightcove server-side ad insertion through the SSAI plugin, including the Google PAL (Programmatic Access Library) nonce used for ad tracking.

## Requirements

- **Platform:** Android phone and tablet.
- **Minimum OS:** Android 5.0 (API 21).
- **Toolchain:** Android Studio, JDK 17, Gradle 8.13.
- **Extra SDKs:** the Brightcove SSAI and thumbnail plugins, resolved from Maven and wired automatically by the build.

## Setup

The samples play from the shared Brightcove demo account (`5420904993001`) with an SSAI-enabled ad configuration.

## Samples

| Sample | Languages | What it demonstrates |
|---|---|---|
| `BasicSsaiSampleApp` | [Java](BasicSsaiSampleApp-java/) · [Kotlin](BasicSsaiSampleApp-kotlin/) | Brightcove server-side ad insertion. |
| `BasicSsaiPALSampleApp` | [Java](BasicSsaiPALSampleApp-java/) · [Kotlin](BasicSsaiPALSampleApp-kotlin/) | SSAI with the Google PAL nonce for ad tracking. |
