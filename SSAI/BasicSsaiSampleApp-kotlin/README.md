# Basic SSAI (BasicSsaiSampleApp)

Plays a video with Brightcove server-side ad insertion using the SSAI plugin (`SSAIComponent`). Registers a companion-ad container, loads a video by ID with an Ad Config ID, and includes an optional Open Measurement toggle.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.kt` | Creates the `SSAIComponent`, registers the companion-ad container, loads a video by ID with an Ad Config ID query parameter through the `Catalog`, and hands the video to the plugin for ad stitching. Also wires an error handler and an Open Measurement tracker toggle. |

See the [SSAI README](../) for shared setup and requirements.
