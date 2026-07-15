# TextureView (TextureViewSampleApp)

Renders playback into a `TextureView` instead of the default `SurfaceView`, via `BrightcoveExoPlayerTextureVideoView`.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.kt` | Fetches a video and plays it in the `TextureView`-backed player. |
| `res/layout/activity_texture_view_sample.xml` | Screen layout hosting the `BrightcoveExoPlayerTextureVideoView`. |

See the [Playback README](../) for shared setup and requirements.
