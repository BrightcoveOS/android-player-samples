# Vertical video (VerticalVideoSampleApp)

A full-bleed vertical (TikTok / Reels style) paging video feed. A `ViewPager2` pages
vertically through a Video Cloud playlist; each page is its own
`BrightcoveExoPlayerVideoView`, cropped to fill the screen, with a poster that fades out
once playback is likely to keep up, tap-to-toggle play/pause, and an on-screen
description.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.java` | Loads the playlist via `Catalog`, wires `ViewPager2` page changes to the adapter, and pauses/resumes the feed with the activity lifecycle. |
| `VerticalVideoAdapter.java` | Owns which page may play: it activates exactly one page, deactivates the rest, releases recycled players, and applies aspect-fill. |
| `res/layout/item_vertical_video.xml` | A single full-screen page: player, poster, play indicator, description scrim. |

## How playback ownership works

This is the core of the sample and the part that is deliberately isolated for reuse:

- `VerticalVideoAdapter` is the **single authority** on which page is allowed to play.
  `ViewPager2.OnPageChangeCallback` calls `activate(position)`; the previous active page
  is always deactivated first, so two pages can never play at once.
- Only the active page queues its video into the SDK. Adjacent pages show their poster
  only, so a swipe does not spin up extra decoders or spend bandwidth on the pages either
  side of the one being watched.
- A page that leaves the active slot is paused and cleared; a holder that is recycled has
  its native player destroyed, so offscreen pages do not leak decoders.
- Each holder remembers the `Video` it is bound to, so a catalog/playback callback that
  resolves after the holder was rebound is ignored.

## Aspect-fill (the Android equivalent of iOS `.resizeAspectFill`)

The Brightcove Android SDK has no `resizeAspectFill` enum. Its render surface exposes a
fit/crop switch:

```kotlin
videoView.renderView?.zoomIn()   // crop to fill
videoView.renderView?.zoomOut()  // fit (letterbox)
```

This is the surface fit/crop switch, **not** the pinch-gesture `ZoomController`. It is the
same call the Brightcove React Native bridge uses for `videoScalingMode="fill"`.

## Reusing this from the React Native bridge

The sample is structured so the mechanics map one-to-one onto a React Native list:

| Native sample | React Native bridge |
|---|---|
| One `BrightcoveExoPlayerVideoView` per page | One `BrightcovePlayerView` mounted per FlatList item |
| `activate(position)` → only the focused page plays | `onViewableItemsChanged` → play only the focused item |
| `deactivate()` on page leave | pause/blur when an item scrolls away |
| `onViewRecycled` → `destroyPlayer()` | unmount the item → dispose the native view |
| `renderView.zoomIn()` for aspect-fill | `videoScalingMode="fill"` (already wired in the bridge) |

The reusable lessons for the bridge are: mount only nearby players, play only the focused
item, stop on blur, release on unmount, and guard recycled/stale callbacks — exactly what
this adapter does natively.

See the [Playback README](../) for shared setup and requirements.
