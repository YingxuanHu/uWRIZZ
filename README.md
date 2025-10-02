# uWRIZZ – Campus Dating App for UW Students

uWRIZZ is an Android-based dating application built with Kotlin, Jetpack Compose, and Firebase for University of Waterloo students. It supports profile creation, preference-driven matching, real-time chat, and Firebase-backed authentication.

---

## Feature Highlights
- Firebase Authentication with email sign-in and profile bootstrap.
- Jetpack Compose UI with dedicated screens for discovery, chat, likes, surveys, and profile management.
- Preference-aware matching backed by Firestore queries and survey scores.
- Local SQLite helper for legacy username/password storage.

---

## Repository Layout
- `app/src/main/java/com/example/uwrizz/`
  - `MainActivity.kt` – entry point that wires navigation and the Compose surfaces.
  - `data/` – local persistence helpers (SQLite).
  - `model/` – data classes shared across screens.
  - `ui/screens/` – composable screens grouped by responsibility.
  - `ui/theme/` – Compose theme setup.
- `app/src/main/res/` – Android resources (layouts, strings, icons).
- `docs/` – project documentation (meeting minutes, etc.).
- `local.properties.example` – template for pointing Gradle at your Android SDK.
- `.vscode/` – VS Code tasks and Java configuration for Gradle/Compose workflows.

`app-debug.apk` and other build products are ignored to keep the tree clean for GitHub.

---

## Prerequisites
- JDK 17 (Temurin, Corretto, or the Android Studio bundled JDK)
- Android SDK with API level 34 (platform, build-tools, platform-tools)
- Firebase project with Authentication and Firestore enabled. A matching `google-services.json` must live in `app/`.
- VS Code with the recommended extensions (automatically surfaced via `.vscode/extensions.json`).

---

## Getting Started
```bash
git clone <your-github-url>/uwrizz.git
cd uwrizz
cp local.properties.example local.properties
# edit local.properties so sdk.dir points to your Android SDK
```

Launch VS Code in the project root and allow it to import the Gradle build. Ensure your shell exposes `JAVA_HOME`:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
```

---

## Running from VS Code
1. Start an Android emulator (Android Studio Device Manager) or connect a device with USB debugging.
2. In VS Code, open the Command Palette → `Run Task`.
   - `gradlew assembleDebug` builds the APK.
   - `gradlew installDebug` builds and deploys to the connected emulator/device.
   - `adb install debug apk` reinstalls the last built debug APK.
3. Debug/observe logs using the VS Code integrated terminal (`adb logcat`) or Android Studio if preferred.

The resulting APK lives at `app/build/outputs/apk/debug/app-debug.apk` whenever you run `assembleDebug`.

---

## Release Notes

### Release Version 0.1.1 (Feb 8, 2024)
- Front-end polish ([issue #10](https://git.uwaterloo.ca/j37chiu1/team-101-6/-/issues/10))
- Login improvements ([issue #9](https://git.uwaterloo.ca/j37chiu1/team-101-6/-/issues/9))
- Chat functionality updates ([issue #8](https://git.uwaterloo.ca/j37chiu1/team-101-6/-/issues/8))
- Backend/database refinements ([issue #7](https://git.uwaterloo.ca/j37chiu1/team-101-6/-/issues/7))

### Release Version 0.1.0 (Feb 2, 2024)
- Likes page ([issue #6](https://git.uwaterloo.ca/j37chiu1/team-101-6/-/issues/6))
- Preference page ([issue #5](https://git.uwaterloo.ca/j37chiu1/team-101-6/-/issues/5))
- Profile settings page ([issue #4](https://git.uwaterloo.ca/j37chiu1/team-101-6/-/issues/4))
- Chat page ([issue #3](https://git.uwaterloo.ca/j37chiu1/team-101-6/-/issues/3))
- Main page ([issue #2](https://git.uwaterloo.ca/j37chiu1/team-101-6/-/issues/2))

---

## Useful Links
- Firebase documentation: https://firebase.google.com/docs
- Android developer guides: https://developer.android.com/docs
