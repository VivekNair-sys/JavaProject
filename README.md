# Campus Course & Records Manager (CCRM) — Minimal submission

**What this repo contains (minimal):**
- A small console Java app to manage students, courses, enrollments, grades, CSV import/export and backup.
- Compact package layout under `src/edu/ccrm`.
- Test CSVs under `test-data/`.

**Project spec (source):** uploaded PDF with full requirements. :contentReference[oaicite:1]{index=1}

## Quick run (JDK 11+)
From project root:
```bash
# compile
mkdir -p out
javac -d out $(find src -name "*.java")

# run
java -cp out edu.ccrm.cli.Main
