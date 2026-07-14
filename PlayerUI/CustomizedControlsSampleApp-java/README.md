# Customized controls (CustomizedControlsSampleApp)

Replaces the default Brightcove media controller with a custom `BrightcoveControlBar` layout, including a dedicated Android TV controller layout. It also adds a custom Font Awesome button to the control bar.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.java` | Hosts the player, selects the phone or Android TV controller layout, and wires the custom Font Awesome button. |
| `res/layout/default_activity_main.xml` | Screen layout hosting the `BrightcoveExoPlayerVideoView`. |
| `res/layout/my_media_controller.xml` | Custom portrait control bar layout. |
| `res/layout-land/my_media_controller.xml` | Custom landscape control bar layout. |
| `res/layout/my_tv_media_controller.xml` | Custom Android TV control bar layout. |
| `res/values/styles.xml` | Enables or disables built-in controller elements via the `BrightcoveControlBar.Custom` styles. |

## Customizing the controls

The controller is defined entirely in the layout XML: each control is a standard Android view assigned one of the SDK's predefined view IDs (such as `@id/play`, `@id/rewind`, or `@id/seek_bar`), which the player recognizes and drives at runtime. Add, remove, or rearrange these views to change the control bar, and add your own views (like the Font Awesome button here) alongside them. See [Customizing the controls](https://sdks.support.brightcove.com/features/customize-controls-brightcove-player-sdk-android.html) for the full list of control IDs.

See the [UI Customization README](../) for shared setup and requirements.
