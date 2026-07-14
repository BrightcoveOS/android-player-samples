# Audio-only playback (AudioOnlySampleApp)

Plays audio-only assets with a media-style background playback notification, so audio and its transport controls keep working when the app is in the background. Layers on:

- a scrollable track list backed by a Brightcove playlist
- shuffle and repeat toolbar actions
- an optional, commented-out example of customizing the media notification's text and action buttons

## Key files

| File | Responsibility |
|---|---|
| `AudioOnlyActivity.kt` | Loads the playlist via `Catalog`, builds the background playback notification, and renders the track list with shuffle/repeat toolbar actions. |

See the [Playback README](../) for shared setup and requirements.
