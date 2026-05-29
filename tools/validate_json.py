#!/usr/bin/env python3
"""Basic JSON syntax validator for Nightseed Bastion data files."""
from pathlib import Path
import json
import sys

ROOT = Path(__file__).resolve().parents[1]
DATA = ROOT / "data"

errors = []
for path in sorted(DATA.glob("*.json")):
    try:
        json.loads(path.read_text(encoding="utf-8"))
        print(f"OK {path.relative_to(ROOT)}")
    except Exception as exc:
        errors.append(f"FAIL {path.relative_to(ROOT)}: {exc}")

if errors:
    print("\n".join(errors))
    sys.exit(1)

print("All JSON files are valid.")
