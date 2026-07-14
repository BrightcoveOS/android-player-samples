# Rewind button (RewindSampleApp)

Adds a rewind button to the Brightcove media controls and sets its icon from a Font Awesome glyph.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.java` | Hosts the player and loads a video from the Brightcove catalog. |
| `res/values/strings.xml` | Sets the rewind button glyph via the `brightcove_controls_rewind` resource, and documents the Font Awesome codepoint options. |
| `res/layout/default_activity_main.xml` | Screen layout hosting the `BrightcoveExoPlayerVideoView`. |

## Choosing the glyph

The rewind icon is a Font Awesome codepoint assigned to the `brightcove_controls_rewind` string resource; edit that value in `res/values/strings.xml` to use a different glyph. See [Customizing the controls](https://sdks.support.brightcove.com/features/customize-controls-brightcove-player-sdk-android.html) for more on the media controller.

See the [UI Customization README](../) for shared setup and requirements.
