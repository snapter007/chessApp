# ChessForge

An Android app for memorizing famous chess openings, traps, and professional-level tactics through repetition, not passive reading.

## How it works

Every opening, trap, and tactic is broken into a scripted move-by-move sequence. At each step:

- **Only the correct piece is interactive.** Every other piece on the board is visually frozen (dimmed) and does nothing when tapped, so your attention and your taps are locked onto the one piece you need to move.
- **Three assist levels** let you control how much help you get:
  - **Guided** - the correct piece glows gold and its destination square is marked. Best for learning a line for the first time.
  - **Piece Only** - the correct piece glows, but you have to recall where it goes yourself.
  - **Master** - no highlighting at all. Full recall, the way a professional drills a line or solves a puzzle. A "Hint" button is available if you get stuck.
- Correct moves flash green, wrong attempts flash red, and progress (practiced / mastered without a mistake) is saved on-device.

## Content

- **15 main-line openings** (Italian Game, Ruy Lopez, Sicilian Najdorf/Dragon, French, Caro-Kann, Queen's Gambit lines, King's Indian, Nimzo-Indian, English, London System, Scandinavian, Scotch).
- **6 famous traps** (Scholar's Mate, Legal's Trap, Fried Liver Attack, Elephant Trap, Blackburne Shilling Gambit, Fool's Mate).
- **13 tactics puzzles** covering forks, pins, skewers, discovered attacks, double attacks, back-rank mates, smothered mate, deflection, decoys, removing the defender, and zwischenzugs.

All move sequences are verified move-by-move against a from-scratch chess engine in `core/` (see `core/src/test`) so every line replays legally.

## Project structure

- `core/` - pure Kotlin chess engine: board representation, FEN parsing, move application (captures, castling, en passant, promotion). No Android dependency, fully unit tested.
- `app/` - the Android app: Jetpack Compose UI, the training state machine, curated content, and on-device progress tracking (DataStore).

## Building the APK

### Option 1: Download a prebuilt APK from GitHub Actions

Every push to this repository builds a debug APK automatically. Go to the **Actions** tab, open the latest **Android Build** run, and download the `chessforge-debug-apk` artifact - it contains an installable `.apk`.

### Option 2: Tag a release

Pushing a tag like `v1.0.0` triggers the **Release APK** workflow, which attaches a ready-to-download `.apk` directly to a GitHub Release:

```
git tag v1.0.0
git push origin v1.0.0
```

### Option 3: Build locally

```
./gradlew :app:assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

### Installing on your phone

Transfer the `.apk` to your Android device and open it (you'll need to allow "install unknown apps" for whichever app you use to open it - Files, Chrome, etc.). No Play Store account or developer setup required.
