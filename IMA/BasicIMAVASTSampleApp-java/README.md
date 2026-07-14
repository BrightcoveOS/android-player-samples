# IMA VAST (BasicIMAVASTSampleApp)

Plays a Brightcove video with Google IMA ads served from a single VAST ad tag. The app schedules preroll, midroll, and postroll ad cue points and presents a companion ad slot alongside the player.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.java` | Loads a video from the `Catalog`, sets preroll/midroll/postroll cue points, configures the Google IMA plugin, answers each ad request with the VAST ad tag, and wires up a companion ad slot. |

See the [IMA ads README](../) for shared setup and requirements.
