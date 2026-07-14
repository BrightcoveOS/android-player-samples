# IMA (Google Interactive Media Ads)

These samples demonstrate client-side ad insertion with Google IMA through the Brightcove IMA plugin — ad rules (server-scheduled ads), a single VAST tag, ads across a list of players, and IMA combined with Widevine DRM content.

## Requirements

- **Platform:** Android phone and tablet.
- **Minimum OS:** Android 5.0 (API 21).
- **Toolchain:** Android Studio, JDK 17, Gradle 8.13.
- **Extra SDKs:** the Brightcove IMA plugin and the Google IMA SDK, both resolved from Maven and wired automatically by the build.

## Setup

The samples play from the shared Brightcove demo account (`5420904993001`) and use Google's sample ad tags. Point the ad tag URL at your own inventory to test real ads.

## Samples

| Sample | Languages | What it demonstrates |
|---|---|---|
| `AdRulesIMASampleApp` | [Java](AdRulesIMASampleApp-java/) · [Kotlin](AdRulesIMASampleApp-kotlin/) | Server-scheduled ads via IMA Ad Rules (VMAP). |
| `BasicIMAVASTSampleApp` | [Java](BasicIMAVASTSampleApp-java/) · [Kotlin](BasicIMAVASTSampleApp-kotlin/) | A single VAST ad tag. |
| `VideoListAdRulesIMASampleApp` | [Java](VideoListAdRulesIMASampleApp-java/) · [Kotlin](VideoListAdRulesIMASampleApp-kotlin/) | IMA Ad Rules across multiple player instances in a list. |
| `AdRulesIMAWidevineModularSampleApp` | [Java](AdRulesIMAWidevineModularSampleApp-java/) · [Kotlin](AdRulesIMAWidevineModularSampleApp-kotlin/) | IMA Ad Rules together with Widevine Modular DRM content. |
