---
name: cleaning-sample-build-dirs
description: Use when reclaiming disk space in this repo by deleting Gradle build output — "remove build folders", "clean the sample apps", "the build dirs are eating GBs". Deletes only per-sample-app build/ folders, never the project-root build/.
---

# Cleaning Sample-App Build Dirs

## Overview

Each sample app in this repo generates a `build/` folder when compiled. Across dozens of sample apps these accumulate to many GB. This skill deletes those per-app build folders **only**, leaving the project-root `build/` untouched.

## Repo layout

Sample apps are organized into capability buckets at the repo root (`Player/`, `PlayerUI/`, `IMA/`, `DAI/`, `SSAI/`, `DRM/`, `Cast/`, `Offline/`, `FreeWheel/`, `Pulse/`, …). Each bucket holds `<AppName>SampleApp-java` and `<AppName>SampleApp-kotlin` module folders. So every sample app lives exactly two levels below the repo root:

```
<repo-root>/<Bucket>/<AppNameSampleApp-lang>/build/   <- delete these (depth 3)
<repo-root>/build/                                     <- NEVER delete (depth 1)
```

## The critical distinction

- **Delete:** any `build/` directory exactly three levels below the repo root (`./<bucket>/<app>/build`). This is where each Gradle module writes its output.
- **NEVER delete:** the project-root `./build/` (one level below the root). Removing it is out of scope and can disrupt the root build.

The `-mindepth 3 -maxdepth 3` filter is what enforces this separation: the root `./build` is at depth 1 and can never be matched, and the filter targets module build folders by position rather than by hardcoded bucket names — so it keeps working if buckets are added, renamed, or removed. Do not loosen the depth filter.

> This same depth-3 rule also sweeps up leftover build folders from an older, pre-reorg layout (e.g. untracked `brightcove-*/<app>/build`), which is desirable.

## Procedure

Run from the repo root. **Always dry-run first, confirm the root `build/` is absent from the list, then delete.**

1. Dry-run — list every folder that will be removed, with sizes and total:
   ```bash
   find . -mindepth 3 -maxdepth 3 -type d -name build -prune -exec du -sh {} +
   ```
   Verify `./build` does NOT appear in the output (it won't, given the depth filter — this is the safety check).

2. Delete:
   ```bash
   find . -mindepth 3 -maxdepth 3 -type d -name build -prune -exec rm -rf {} +
   ```

3. Confirm the root build folder survives and app builds are gone:
   ```bash
   ls -d ./build && echo "root build preserved"
   find . -mindepth 3 -maxdepth 3 -type d -name build | wc -l   # expect 0
   ```

## Common mistakes

- **Using `./gradlew clean` instead** — far slower, requires a working Gradle setup, and won't remove build dirs of modules excluded from the current build (e.g. FreeWheel/Pulse samples that are gated on optional dependencies). `rm -rf` on the folders is direct and reliable for pure disk reclamation.
- **Dropping the depth filter** (e.g. `find . -type d -name build -exec rm -rf`) — this would also delete the project-root `./build`, and would recurse into nested `build/` subfolders redundantly. Keep `-mindepth 3 -maxdepth 3`.
- **Hardcoding bucket names** — target build folders by depth/position, not by matching `Player`, `IMA`, etc. Buckets change; the depth-3 rule doesn't.
- **Skipping the dry-run** — always eyeball the list first; it's the only check that the scope is right.
- **Deleting `.gradle`, `.idea`, or `.cxx` folders too** — out of scope. This skill targets `build/` only.
