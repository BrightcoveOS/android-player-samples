# IMA ad rules (AdRulesIMASampleApp)

Plays a Brightcove video with Google IMA ads scheduled server-side through IMA ad rules (VMAP), so the ad server decides where prerolls, midrolls, and postrolls appear. Ad markers are drawn on the seek bar at the positions the ads manager reports.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.kt` | Loads a video from the `Catalog`, configures the Google IMA plugin with ad rules enabled, answers the ad request with a VMAP ad tag URL, and adds ad markers to the seek bar. |

See the [IMA ads README](../) for shared setup and requirements.
