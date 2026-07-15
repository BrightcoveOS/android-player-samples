# IMA ads in a video list (VideoListAdRulesIMASampleApp)

Shows multiple independent Brightcove player instances in a scrolling list, where each row plays its own video and requests Google IMA ad-rules (VMAP) ads. Demonstrates attaching the IMA plugin per player and managing playback as rows scroll on and off screen.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.java` | Hosts the `RecyclerView`, builds a `Catalog`, and loads a playlist by reference ID, handing its videos to the adapter. |
| `VideoListAdapter.java` | `RecyclerView.Adapter` that creates a `BrightcoveExoPlayerVideoView` per row, wires up the Google IMA ad-rules plugin with a VMAP ad tag, and starts/stops each player as rows attach and detach. |

See the [IMA ads README](../) for shared setup and requirements.
