# AppCompat integration (AppCompatSampleApp)

Uses the Brightcove AppCompat plugin to run the player inside AndroidX AppCompat components. A launcher screen lets you choose between the two supported player entry points:

- an Activity that extends `BrightcovePlayerActivity`
- an `AppCompatActivity` that hosts a `BrightcovePlayerFragment`

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.kt` | Launcher screen; routes to the Activity or Fragment example. |
| `AppCompatPlayerActivity.kt` | Player screen extending `BrightcovePlayerActivity` via the AppCompat plugin. |
| `FragmentPlayerActivity.kt` | `AppCompatActivity` that hosts `PlayerFragment`. |
| `PlayerFragment.kt` | `BrightcovePlayerFragment` that loads and plays the sample video. |

See the [Playback README](../) for shared setup and requirements.
