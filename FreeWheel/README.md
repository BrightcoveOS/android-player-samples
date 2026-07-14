# FreeWheel

These samples demonstrate ad insertion with FreeWheel through the Brightcove FreeWheel plugin, including FreeWheel ads combined with Widevine DRM content.

## Requirements

- **Platform:** Android phone and tablet.
- **Minimum OS:** Android 5.0 (API 21).
- **Toolchain:** Android Studio, JDK 17, Gradle 8.13.
- **Extra SDKs:** the FreeWheel **`AdManager.aar` (v6.28.0)**, which Brightcove does not distribute. Obtain it from [FreeWheel](http://freewheel.tv/about/#contact-us) and place it in a `libs/` folder in your home directory (`~/libs/AdManager.aar`).

## Setup

The samples play from the shared Brightcove demo account (`5420904993001`) with FreeWheel ad configuration. The build only includes the **Java** FreeWheel samples when `~/libs/AdManager.aar` is present; the Kotlin samples are always included and also require the AAR to build.

## Samples

| Sample | Languages | What it demonstrates |
|---|---|---|
| `FreeWheelSampleApp` | [Java](FreeWheelSampleApp-java/) · [Kotlin](FreeWheelSampleApp-kotlin/) | FreeWheel ads. |
| `FreeWheelWidevineModularSampleApp` | [Java](FreeWheelWidevineModularSampleApp-java/) · [Kotlin](FreeWheelWidevineModularSampleApp-kotlin/) | FreeWheel ads with Widevine Modular DRM content. |
