# SSAI with PAL (BasicSsaiPALSampleApp)

Combines Brightcove server-side ad insertion with the Google PAL (Programmatic Access Library) nonce for ad tracking.

## Key files

| File | Responsibility |
|---|---|
| `BasicSsaiPALSampleAppActivity.kt` | Generates a PAL nonce via `NonceLoader`/`NonceManager`, passes it to the `SSAIComponent`, and loads a video by ID with an Ad Config ID for ad stitching. Forwards playback-start, playback-end, and ad-click signals to PAL, and wires an Open Measurement toggle. |

See the [SSAI README](../) for shared setup and requirements.
