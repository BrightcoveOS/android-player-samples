# Live / DVR (LiveSampleApp)

Plays HLS Live and Live DVR content with the live control layout, which shows a live indicator and DVR seek controls for streams that support them.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.java` | Builds an HLS Live video from placeholder values and starts playback. |

## Supplying a stream

This sample does not ship with a playable stream: live streams are ephemeral, so Brightcove cannot bundle a permanent one. In `MainActivity.java`, replace the `YOUR_LIVE_HLS_STREAM` placeholder with your own Video Cloud Live/DVR HLS stream URL and the `YOUR_VIDEOCLOUD_PUBLISHER_ID` placeholder with your publisher id. Until you do, the app builds and launches but has nothing to play.

See the [Playback README](../) for shared setup and requirements.
