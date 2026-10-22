#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

pretty() {
  local file="$1"
  if [[ -f "$file" ]]; then
    python3 -c "import json,pathlib; p=pathlib.Path('$file'); p.write_text(json.dumps(json.loads(p.read_text()), indent=2, ensure_ascii=False) + '\n', encoding='utf-8')"
    echo "Pretty-printed: $ROOT/$file"
  else
    echo "Skip (not found): $ROOT/$file"
  fi
}

pretty target/spotbugsSarif.json
pretty target/dependency-check-report.json
