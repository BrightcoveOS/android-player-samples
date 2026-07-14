# 360 video (360VideoSampleApp)

Plays a 360° equirectangular video, which the SDK renders with touch and gyroscope navigation.

## Key files

| File | Responsibility |
|---|---|
| `Video360Activity.kt` | Fetches the video, detects its equirectangular projection, and starts playback. |
| `res/layout/activity_main.xml` | Screen layout hosting the `BrightcoveExoPlayerVideoView`. |

See the [Playback README](../) for shared setup and requirements.
