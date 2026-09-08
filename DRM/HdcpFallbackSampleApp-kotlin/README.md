# HDCP Fallback (HdcpFallbackSampleApp)

Plays a Widevine video whose account uses HDCP fallback: each rendition tier carries a DRM key of
its own, the SD key plays on any output, and the HD key only plays over an HDCP-protected one.

The sample keeps both classes of device playing. A protected output adapts up to the HD renditions.
An unprotected output stays on the SD renditions, instead of failing with a decrypt error that the
player cannot recover from.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.kt` | Attaches the HDCP guard, loads the video and logs every rendition change. |
| `HdcpFallback.kt` | Reads the HDCP level of the connected output and constrains rendition selection to the SD tier while that output is unprotected. Re-reads the level when a display is attached or detached. |

## How it works

1. On every `SET_SOURCE` the guard marks the Widevine source with `multiSession`, because moving
   between rendition tiers needs one concurrent DRM session per key.
2. It then reads `MediaDrm.getConnectedHdcpLevel()`, or the vendor `hdcpLevel` property on API 27
   and below, and compares it with the level the licence policy of the account requires for HD.
3. While the output does not reach that level, the guard caps the track selector at SD
   (1279x719) and turns off `exceedVideoConstraintsIfNecessary`, so output protection fails closed.
4. A `DisplayManager.DisplayListener` repeats the check when a display is attached or detached, for
   example when an HDMI cable is plugged in during playback.

## Two shapes of manifest

A Brightcove DASH manifest carries the rendition tiers as separate AdaptationSets. Whether the
player can adapt across them depends on the manifest:

- Accounts with codec whitelisting get a manifest that links the AdaptationSets with
  `urn:mpeg:dash:adaptation-set-switching:2016`, and the player treats the whole ladder as one.
- Accounts without it get a manifest whose AdaptationSets stand alone. The player then adapts
  inside one tier only, and the SDK steers it to the tier that holds the largest rendition the
  current constraints allow.

The guard behaves the same either way, which is the point: it constrains selection rather than
picking renditions.

## Requirements and limits

- The account must have HDCP fallback enabled, so that the manifest carries a separate key per
  rendition tier. Without it the video has one key and there is nothing to fall back to.
- `MediaDrm.HDCP_NO_DIGITAL_OUTPUT` means the device has no digital output at all and is therefore
  implicitly secure. It is the highest level, not the lowest, so a phone with nothing plugged in
  plays HD.
- `MediaDrm.HDCP_LEVEL_UNKNOWN` restricts playback to SD, because a wrong guess costs the viewer
  the whole playback.
- The guard constrains automatic selection. A custom quality menu has to refuse the renditions
  above the cap as well, because an explicit track selection override bypasses a size constraint.
- Cast playback is not affected by the HDCP state of the sending device.

See the [DRM README](../) for shared setup and requirements.
