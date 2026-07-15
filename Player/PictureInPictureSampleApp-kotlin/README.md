# Picture-in-Picture (PictureInPictureSampleApp)

Enters Android Picture-in-Picture mode so playback continues in a small floating window while the user leaves the app. A companion settings screen lets you adjust PiP behavior such as closed-caption display, aspect ratio, and whether entering the home screen triggers PiP.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.kt` | Fetches a video and applies saved PiP settings on resume. |
| `SettingsActivity.kt` | Hosts the preference screen for editing PiP options. |
| `SettingsModel.kt` | Reads PiP preferences (captions, scale factor, aspect ratio) from shared preferences. |

See the [Playback README](../) for shared setup and requirements.
