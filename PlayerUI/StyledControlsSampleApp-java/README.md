# Styled controls (StyledControlsSampleApp)

Restyles the default and Android TV media controllers by overriding the Brightcove control styles in `styles.xml`. It changes button colors, swaps the seek-bar thumb, and restyles the Android TV player-options menu, all without editing a controller layout.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.java` | Hosts the player and plays the demo video; the restyled controls are applied automatically from resources. |
| `res/layout/default_activity_main.xml` | Screen layout hosting the `BrightcoveExoPlayerVideoView`. |
| `res/values/styles.xml` | Overrides the Brightcove control bar, seek bar, button, and Android TV player-options styles. |
| `res/drawable-hdpi/scrubber.png` | Custom seek-bar thumb referenced by the seek bar style. |
| `res/drawable/custom_player_option_item_text.xml` | Color selector for the Android TV player-options radio button text. |

## Styling the controls

Styling is applied by overriding the SDK's `Brightcove*` styles (`BrightcoveControlBar`, `BrightcoveSeekBar`, `BorderlessButton`, and their `.TV` variants) in `styles.xml`. Keep the same style names and parent from the corresponding `*Default` style, and the default controller layout picks up your changes automatically. See [Customizing the controls](https://sdks.support.brightcove.com/features/customize-controls-brightcove-player-sdk-android.html) for the available style attributes.

See the [UI Customization README](../) for shared setup and requirements.
