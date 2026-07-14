# Video list / playlist (VideoListSampleApp)

Shows multiple independent Brightcove player instances in a scrolling `RecyclerView`, one per playlist entry. Each row hosts its own `BrightcoveExoPlayerVideoView`, and the sample manages per-player lifecycle and playhead position as rows scroll on and off screen.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.java` | Fetches a playlist via `Catalog` and populates the RecyclerView. |
| `AdapterView.java` | RecyclerView adapter; builds a player per row and manages its playback lifecycle. |

See the [Playback README](../) for shared setup and requirements.
