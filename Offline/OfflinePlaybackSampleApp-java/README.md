# Offline playback (OfflinePlaybackSampleApp)

Downloads Brightcove Video Cloud videos to the device for offline viewing and manages those downloads. Lists videos with their download state, tracks progress, and handles rental and purchase licenses for DRM-protected content.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.java` | Entry activity; builds the `OfflineCatalog`, shows the playlist when online or completed downloads when offline, listens for connectivity and download events, and handles rent/buy license requests and playback. |
| `VideoListAdapter.java` | `RecyclerView` adapter that renders each video with its download status, estimated size, progress bar, and rent/buy/download/pause/resume/delete controls. |
| `VideoListListener.java` | Interface defining the video-item interaction callbacks (play, rent, buy, download, pause, resume, delete). |
| `PlaylistModel.java` | Immutable DTO that identifies a playlist by id or reference id and finds it in the catalog. |
| `DatePickerFragment.java` | Date-picker dialog for choosing a rental expiry date. |
| `BrightcoveDownloadUtil.java` | Selects which audio and caption tracks are included in a download. |
| `ViewUtil.java` | Helper for looking up child views by resource id. |

See the [Offline playback README](../) for shared setup and requirements.
