# FreeWheel ads (FreeWheelSampleApp)

Requests and plays FreeWheel ads through the FreeWheel plugin alongside a Brightcove video.

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.java` | Creates a `FreeWheelController`, configures the ad network, profile, and site section, and requests preroll, midroll, postroll, overlay, and companion ad slots. Renders returned display ads into the ad container and plays them with the video. |

See the [FreeWheel README](../) for shared setup and requirements.
