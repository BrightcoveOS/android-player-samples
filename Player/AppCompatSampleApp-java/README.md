# AppCompat integration (AppCompatSampleApp)

Uses the Brightcove AppCompat plugin to run the player inside AndroidX AppCompat components. A launcher screen lets you choose between the two supported player entry points:

- an Activity that extends `BrightcovePlayerActivity`
- an `AppCompatActivity` that hosts a `BrightcovePlayerFragment`

## Key files

| File | Responsibility |
|---|---|
| `MainActivity.java` | Launcher screen; routes to the Activity or Fragment example. |
| `AppCompatPlayerActivity.java` | Player screen extending `BrightcovePlayerActivity` via the AppCompat plugin. |
| `FragmentPlayerActivity.java` | `AppCompatActivity` that hosts `PlayerFragment`. |
| `PlayerFragment.java` | `BrightcovePlayerFragment` that loads and plays the sample video. |

See the [Playback README](../) for shared setup and requirements.
