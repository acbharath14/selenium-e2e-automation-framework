#!/usr/bin/env bash
#
# Preflight checks for selenium-e2e-automation-framework.
#
# Verifies Java 17+, Maven, and a Chrome/Chromium binary are available,
# installing what is missing (macOS via the most common package managers,
# Linux via apt). Browsers/drivers are checked, not installed — the script
# prints the fix command instead.
#
# Runs automatically inside `mvn test` (bound to the `validate` phase in
# pom.xml). Skip with:  mvn test -Dpreflight.skip=true
#
set -u

SKIP="${PREFLIGHT_SKIP:-false}"
if [ "$SKIP" = "true" ]; then
  echo "preflight: skipped (PREFLIGHT_SKIP=true)"
  exit 0
fi

fail=0

have() { command -v "$1" >/dev/null 2>&1; }

echo "preflight: checking Java..."
if have java; then
  ver="$(java -version 2>&1 | head -1)"
  echo "  found: $ver"
  major="$(echo "$ver" | sed -E 's/.*version "([0-9]+).*/\1/')"
  if [ "${major:-0}" -lt 17 ]; then
    echo "  ERROR: Java 17+ is required."
    fail=1
  fi
else
  echo "  Java not found."
  if have brew; then
    echo "  installing via Homebrew..."
    brew install --cask temurin@17 || fail=1
  else
    echo "  Fix: install a Temurin 17 JDK from https://adoptium.net/temurin/releases/"
    fail=1
  fi
fi

echo "preflight: checking Maven..."
if have mvn; then
  echo "  found: $(mvn -version 2>&1 | head -1)"
else
  echo "  Maven not found."
  if have brew; then
    echo "  installing via Homebrew..."
    brew install maven || fail=1
  elif have apt-get; then
    echo "  installing via apt..."
    sudo apt-get update -qq && sudo apt-get install -y -qq maven || fail=1
  else
    echo "  Fix: install Maven from https://maven.apache.org/download.cgi and add it to PATH"
    fail=1
  fi
fi

echo "preflight: checking Chrome/Chromium..."
if have google-chrome || have chromium || have chromium-browser || [ -d "/Applications/Google Chrome.app" ]; then
  echo "  found a Chrome/Chromium binary"
else
  echo "  WARNING: no Chrome/Chromium found."
  echo "  Selenium Manager downloads the *driver*, not the browser itself."
  echo "  Fix (macOS): brew install --cask google-chrome"
  echo "  Fix (Linux): sudo apt-get install -y chromium-browser"
  fail=1
fi

if [ "$fail" -ne 0 ]; then
  echo "preflight: FAILED — fix the items above, then re-run."
  echo "  (Skip this check with: mvn test -Dpreflight.skip=true)"
  exit 1
fi

echo "preflight: OK"
