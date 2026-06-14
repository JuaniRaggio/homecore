#!/usr/bin/env bash
#
# Dev helper to build, install and run HomeCore on an emulator without Android Studio.
#
# Usage:
#   ./run.sh            build + install + launch (boots the emulator if needed)
#   ./run.sh logs       stream the app logs (logcat filtered to HomeCore)
#   ./run.sh stop       kill the running emulator
#
# Requires the Android SDK at ~/Library/Android/sdk and an AVD (defaults to Pixel_10).

set -euo pipefail

SDK="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
ADB="$SDK/platform-tools/adb"
EMULATOR="$SDK/emulator/emulator"
AVD="${AVD_NAME:-Pixel_10}"
PKG="com.itba.homecore"
APK="app/build/outputs/apk/debug/app-debug.apk"

cd "$(dirname "$0")"

boot_emulator() {
  if "$ADB" devices | grep -q "emulator-.*device"; then
    echo "Emulator already running."
    return
  fi
  echo "Booting emulator '$AVD'..."
  "$EMULATOR" -avd "$AVD" -netdelay none -netspeed full >/dev/null 2>&1 &
  "$ADB" wait-for-device
  echo "Waiting for boot to complete..."
  until [ "$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" = "1" ]; do
    sleep 3
  done
  echo "Boot complete."
}

case "${1:-run}" in
  logs)
    exec "$ADB" logcat --pid="$("$ADB" shell pidof "$PKG")" -v color
    ;;
  stop)
    "$ADB" emu kill && echo "Emulator stopped."
    ;;
  run)
    boot_emulator
    echo "Building debug APK..."
    ./gradlew assembleDebug -q
    echo "Installing..."
    "$ADB" install -r "$APK"
    echo "Launching..."
    "$ADB" shell monkey -p "$PKG" -c android.intent.category.LAUNCHER 1 >/dev/null 2>&1
    echo "Done. App running on the emulator."
    ;;
  *)
    echo "Unknown command: $1 (use: run | logs | stop)"
    exit 1
    ;;
esac
