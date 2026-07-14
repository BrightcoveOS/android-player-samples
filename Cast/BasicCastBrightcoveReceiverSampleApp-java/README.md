# Casting with the Brightcove receiver (BasicCastBrightcoveReceiverSampleApp)

Adds Google Cast support to a Brightcove player and casts to the Brightcove Cast Receiver. Browse a list of videos from a Video Cloud playlist and play each one locally or send it to a connected Cast device, with optional server-side ad insertion.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.java` | Launcher activity; loads the demo playlist through the Catalog, shows it in a `RecyclerView`, adds the Cast media-route button, casts a splash screen when a session starts, and launches the player for the selected video. |
| `VideoPlayerActivity.java` | Plays the chosen video locally or on the Cast device; builds the Brightcove Cast custom data, wires up `GoogleCastComponent`, and optionally processes SSAI ads. |
| `VideoListAdapter.java` | `RecyclerView` adapter that renders each video's thumbnail, title, description, and duration and reports item clicks. |
| `Constants.java` | Intent-extra keys and video-property keys shared across the activities. |

See the [Casting README](../) for shared setup and requirements.
