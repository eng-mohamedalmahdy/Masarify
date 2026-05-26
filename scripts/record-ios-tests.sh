#!/usr/bin/env bash
# Poll for a booted iOS simulator, then record video until killed (SIGTERM).
# Usage: bash scripts/record-ios-tests.sh <output_path>
set -e
OUTPUT="$1"
MAX_WAIT=90

for i in $(seq 1 $MAX_WAIT); do
    UDID=$(xcrun simctl list devices --json 2>/dev/null \
        | python3 -c "
import json, sys
data = json.load(sys.stdin)
for devs in data.get('devices', {}).values():
    for d in devs:
        if d.get('state') == 'Booted' and d.get('isAvailable', True):
            print(d['udid']); exit()
" 2>/dev/null)
    if [ -n "$UDID" ]; then
        echo "[ios-recorder] Simulator booted: $UDID — starting recording"
        exec xcrun simctl io "$UDID" recordVideo --force "$OUTPUT"
    fi
    sleep 1
done

echo "[ios-recorder] No simulator booted within ${MAX_WAIT}s" >&2
exit 1
