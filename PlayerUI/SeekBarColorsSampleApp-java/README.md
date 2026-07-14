# Seek-bar colors (SeekBarColorsSampleApp)

Customizes the colors of the Brightcove media controller's seek bar.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.java` | Hosts the player and loads a video from the Brightcove catalog. |
| `res/values/colors.xml` | Recolors the seek bar by overriding the `bmc_seekbar_played`, `bmc_seekbar_buffered`, and `bmc_seekbar_track` colors. |
| `res/layout/default_activity_main.xml` | Screen layout hosting the `BrightcoveExoPlayerVideoView`. |

## Recoloring the seek bar

The seek bar is recolored entirely by overriding the SDK's named `bmc_seekbar_*` color resources in `res/values/colors.xml`, with no code changes. See [Customizing the controls](https://sdks.support.brightcove.com/features/customize-controls-brightcove-player-sdk-android.html) for more on the media controller.

See the [UI Customization README](../) for shared setup and requirements.
