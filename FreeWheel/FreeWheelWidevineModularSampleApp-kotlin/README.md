# FreeWheel ads with Widevine DRM (FreeWheelWidevineModularSampleApp)

Plays FreeWheel ads over Widevine Modular DRM-protected content.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.kt` | Creates a `FreeWheelController` and requests preroll, midroll, postroll, overlay, and companion ad slots, then loads a DRM-protected video by ID through the `Catalog` and plays it with the requested ads. |

See the [FreeWheel README](../) for shared setup and requirements.
