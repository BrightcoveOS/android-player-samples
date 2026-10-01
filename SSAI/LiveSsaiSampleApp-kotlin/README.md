# Live SSAI (LiveSsaiSampleApp)

Plays a NextGen Live 2.0 stream with Brightcove server-side ad insertion using the SSAI plugin (`SSAIComponent`).

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.kt` | Creates the `SSAIComponent`, loads the live video by ID from the Playback API with the Ad Config ID and the live playback token as query parameters, and hands the video to the plugin. |

## Setup

The sample has no demo stream. Before you run it, set these values in `src/main/res/values/strings.xml` for a NextGen Live 2.0 stream that has SSAI enabled:

- `sdk_demo_account`: the account ID
- `sdk_demo_policy`: a policy key for the account
- `sdk_demo_video_id`: the live video ID
- `sdk_demo_ad_config_id`: the SSAI ad config ID
- `sdk_demo_live_playback_token`: a live playback token with SSAI enabled

See the [SSAI README](../) for shared setup and requirements.
