# Pulse

These samples demonstrate ad insertion with Pulse through the Brightcove Pulse plugin.

## Requirements

- **Platform:** Android phone and tablet.
- **Minimum OS:** Android 5.0 (API 21).
- **Toolchain:** Android Studio, JDK 17, Gradle 8.13.
- **Extra SDKs:** the **`Pulse.aar`**, which Brightcove does not distribute. Place it in a `libs/` folder in your home directory (`~/libs/Pulse.aar`).

## Setup

The samples play from the shared Brightcove demo account (`5420904993001`) with Pulse ad configuration. Both language samples are included in the build only when `~/libs/Pulse.aar` is present.

## Samples

| Sample | Languages | What it demonstrates |
|---|---|---|
| `PulseSampleApp` | [Java](PulseSampleApp-java/) · [Kotlin](PulseSampleApp-kotlin/) | Pulse ads. |
