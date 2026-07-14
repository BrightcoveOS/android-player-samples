# Audio-only playback (AudioOnlySampleApp)

Plays audio-only assets with a media-style background playback notification, so audio and its transport controls keep working when the app is in the background. Layers on:

- a scrollable track list backed by a Brightcove playlist
- shuffle and repeat controls

## Key files

| File | Responsibility |
|---|---|
| `AudioOnlyActivity.kt` | Loads the playlist via `Catalog`, wires the shuffle and repeat controls, attaches the background playback notification, and renders the track list. |

See the [Playback README](../) for shared setup and requirements.
