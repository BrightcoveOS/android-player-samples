# Casting (Chromecast)

These samples showcase Google Cast support through the Brightcove Cast plugin, sending playback from the app to a Cast device using the Brightcove Cast Receiver.

## Requirements

- **Platform:** Android phone and tablet, plus a Cast-capable device (e.g. Chromecast) on the same network.
- **Minimum OS:** Android 5.0 (API 21).
- **Toolchain:** Android Studio, JDK 17, Gradle 8.13.
- **Extra SDKs:** the Brightcove Cast plugin, resolved from Maven and wired automatically by the build.

## Setup

The sample plays from the shared Brightcove demo account (`5420904993001`) and casts to the Brightcove Cast Receiver. The Cast receiver application id is configured in the sample's options provider.

## Samples

| Sample | Languages | What it demonstrates |
|---|---|---|
| `BasicCastBrightcoveReceiverSampleApp` | [Java](BasicCastBrightcoveReceiverSampleApp-java/) · [Kotlin](BasicCastBrightcoveReceiverSampleApp-kotlin/) | Google Cast with the Brightcove Cast Receiver. |
