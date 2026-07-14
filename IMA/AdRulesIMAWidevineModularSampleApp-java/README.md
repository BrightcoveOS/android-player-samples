# IMA ad rules with Widevine DRM (AdRulesIMAWidevineModularSampleApp)

Plays Widevine Modular DRM-protected Brightcove content with Google IMA ads scheduled server-side through IMA ad rules (VMAP). The ad server decides where prerolls, midrolls, and postrolls appear during protected playback.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.java` | Loads the Widevine-protected video from the `Catalog`, configures the Google IMA plugin with ad rules enabled, and answers the ad request with the configured VMAP ad tag URL. |

See the [IMA ads README](../) for shared setup and requirements.
