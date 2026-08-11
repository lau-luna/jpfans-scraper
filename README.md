# jpfans-scraper
 
A terminal-based watcher for JPFans (a proxy for buying and shipping internationally from platforms like Mercari) that polls a keyword search — e.g. `psp junk` — with configurable filters like max price, and notifies me when new matching listings show up.
 
## Table of contents
 
- [Purpose](#purpose)
- [Current libraries used](#current-libraries-used)
- [Documentation](#documentation)
- [TODOs](#todos)
- [Releases](#releases)
## Purpose
 
JPFans doesn't offer search alerts or notifications on its own, and manually refreshing a search page to catch new junk/parts listings is tedious. This project runs as a terminal application (TUI) — no GUI, no browser required — that periodically queries JPFans' search for a given keyword and set of filters (price range, etc.), keeps track of what's already been seen, and notifies me when something new comes up.
 
Running entirely in the terminal keeps it lightweight enough to leave running in the background on any machine, without needing a desktop environment.
 
## Current libraries used
 
- **Lanterna**: for building TUIs in Java.
- **Gson**: for JSON parsing (see [`docs/cheatsheet-gson.md`](docs/cheatsheet-gson.md)).
- **sqlite-jdbc**: for SQLite database handling (tracking already-seen listings for dedupe).
- `java.net.http.HttpClient`: built-in, used to call JPFans' internal search API directly (see [`docs/cheatsheet-httpclient.md`](docs/cheatsheet-httpclient.md)).
**Jsoup was dropped**: initially planned for HTML scraping, but reverse engineering JPFans' frontend (see docs below) turned up an internal JSON search API that the site itself uses — calling it directly returns structured, already-translated data, with no HTML parsing needed. Notes on Jsoup are kept in [`docs/jsoup.md`](docs/jsoup.md) in case scraping is ever needed again as a fallback.
 
## Documentation
 
- [`docs/reverse-engineering-jpfans.md`](docs/reverse-engineering-jpfans.md) — notes on JPFans' internal search API, reverse-engineered from devtools: request/response shapes, available filters, sort values, pagination behavior, and Cloudflare behavior.
- [`docs/cheatsheet-gson.md`](docs/cheatsheet-gson.md) — Gson basics.
- [`docs/cheatsheet-httpclient.md`](docs/cheatsheet-httpclient.md) — `java.net.http.HttpClient` basics.
- [`docs/jsoup.md`](docs/jsoup.md) — Jsoup basics, kept as a fallback reference.
## TODOs
 
- [ ] Make a JUnit suite to test the parsing process, fetching a local HTML file downloaded straight from JPfans.
## Releases
 
No precompiled binaries or packaged releases yet — for now the project has to be built and run from source with Gradle.
