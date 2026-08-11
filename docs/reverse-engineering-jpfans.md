# JPFans API — Reverse Engineering Notes

JPFans is a proxy to buy and ship internationally from platforms like Mercari or JDirectItems.

It isn't a SPA — the product list is indexed and returned by a plain HTTP GET, working even without an account. I must check if there's some form of product shadowing/limiting for non-logged-in users.

## GET requests

Base url is `https://jpfans.com/`

### Searching by keywords (page load)

You can search by keywords, including spaces, directly in the URL, performing a GET request. The query parameter `searchType` must be passed with the value `keywords`.

Like this:

`https://jpfans.com/psp%20junk/search/mercari?searchType=keywords`

That would search for "psp junk" using direct search, which does **not** translate the keywords to Japanese — it just searches Mercari with the raw English string.

## Internal search API (used by the frontend, reverse-engineered from devtools)

The page above doesn't build the result list itself — it calls a set of internal JSON endpoints under `/search-info/` to validate input, fetch available filters, and run the actual search **with keyword translation**. These are the ones worth calling directly instead of scraping the rendered HTML.

### `GET /search-info/user-input`

Validates/normalizes the raw search input and detects which platform it maps to.

`https://jpfans.com/search-info/user-input?input=psp game disc&platform=mercari&site=jp&lang=en&language=en&wmc-currency=USD`

Response:
```json
{"code":200,"data":{"platform":"mercari","resultType":"keywords","resultPayload":"psp game disc","disabled":false,"country":"AR"},"msg":"操作成功"}
```

### `GET /search-info/config/rule`

Returns which filters are available/configurable for a given platform.

`https://jpfans.com/search-info/config/rule?platform=mercari&site=jp&lang=en&language=en&wmc-currency=USD`

Response tells you, per filter, `isAvailable` and `isCustomConfig`. For `mercari`: `keyword`, `excludeKeyword`, and `priceRange` are custom-configurable — that's the field to use for a max price filter. `productCondition` is available but not custom-config, so its valid values likely need to come from another config endpoint (not found yet — check what the condition dropdown in the UI sends).

### `POST /search-info/search`

The actual search call. Returns items already translated to English (via `nameI18n`).

`https://jpfans.com/search-info/search?lang=en&language=en&wmc-currency=USD`

Body:
```json
{
  "platform": "mercari",
  "cacheDisabled": false,
  "category": [],
  "keyword": "psp junk",
  "excludeKeyword": "",
  "itemTypes": [],
  "brands": [],
  "productCondition": [],
  "sizes": [],
  "auctionOptions": "",
  "priceOptions": "",
  "priceMin": 0,
  "priceMax": 0,
  "shippingCost": [],
  "colors": [],
  "page": 1,
  "pageSize": 40,
  "sort": "1",
  "shopId": "",
  "userId": "",
  "translateKeywords": true,
  "lang": "en",
  "language": "en",
  "site": "jp"
}
```

Relevant fields for my use case:
- `keyword` — search term, in English. `translateKeywords: true` makes the backend translate it to Japanese before querying Mercari.
- `priceMin` / `priceMax` — price filter, `0` seems to mean "no limit". Currency presumably follows `wmc-currency` in the query string.
- `excludeKeyword` — useful for filtering out noise.
- `page` / `pageSize` — pagination, `40` appears to be the max page size the UI uses.
- `sort` — sorting order for product search results (see table below for references).

### Sort values (`sort` field in the search POST body)

Discovered by testing the sort dropdown in the UI:

| Value | Meaning |
|-------|---------|
| `1` | Recommended order (default if `sort` is omitted) |
| `2` | Latest first |
| `3` | Price: low to high |
| `4` | Price: high to low |
| `5` | "I like sorting" — presumably randomized order |

### Pagination behavior

`pageSize` is capped server-side (see below) regardless of the value sent — but `page`
still works normally as an offset over the server's fixed page size. Consecutive pages
can overlap slightly (a couple of items from the end of one page reappear at the start
of the next), likely because the underlying Mercari result set shifts between calls as
listings are added/removed. Not an issue for polling with id-based dedupe.

### `pageSize` cap

Requesting a high `pageSize` (e.g. `9999`) does not return that many items — the API
caps the result count server-side. Observed with `jq '.data.items | length'` across
different high-inventory keywords:

| Keyword | Items returned |
|---------|-----------------|
| `ps vita junk` | 90 |
| `ps vita games` | 90–96 (varied across runs) |
| `playstation 2` | 95 |

Not a confirmed fixed round number — seems to hover in the ~90-96 range regardless of
how high `pageSize` is set. For practical purposes, don't request more than ~90-95;
the API won't return more anyway.

Response item shape:
```json
{
  "id": "m84793824742",
  "name": "PSP2000 アイスシルバー ジャンク品",
  "nameI18n": "PSP-2000 Ice Silver, Junk Item",
  "price": "5000",
  "image": "https://static.mercdn.net/thumb/item/jpeg/m84793824742_1.jpg?...",
  "internalCode": ""
}
```
Note: `price` comes back as a string, needs parsing. `id` is the Mercari item id — this is what I should use as the dedupe key for polling.

### Minimal required headers for the POST

Confirmed via `curl` testing — the request fails (returns HTML instead of JSON) if
`Accept` or `User-Agent` are missing. Formalized minimal request:

```bash
curl -X POST 'https://jpfans.com/search-info/search?lang=en&language=en&wmc-currency=USD' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:153.0) Gecko/20100101 Firefox/153.0' \
  -d '{
    "platform": "mercari",
    "cacheDisabled": false,
    "category": [],
    "keyword": "psp junk",
    "excludeKeyword": "",
    "itemTypes": [],
    "brands": [],
    "productCondition": [],
    "sizes": [],
    "auctionOptions": "",
    "priceOptions": "",
    "priceMin": 0,
    "priceMax": 0,
    "shippingCost": [],
    "colors": [],
    "page": 1,
    "pageSize": 90,
    "sort": "2",
    "shopId": "",
    "userId": "",
    "translateKeywords": true,
    "lang": "en",
    "language": "en",
    "site": "jp"
  }'
```

Required headers:
- `Content-Type: application/json` — otherwise the body isn't parsed as JSON server-side.
- `Accept: application/json` — without this, got back an HTML response instead of JSON.
- `User-Agent` — a real browser UA string; not confirmed if any UA works or if it needs to look legitimate, but a blank/default `curl` UA didn't produce JSON.

No cookies or auth tokens needed (see Cloudflare section below).

### Cloudflare

The site is behind Cloudflare (`cf_clearance` cookie observed on requests made from the browser). Confirmed via `curl` **without** any cookies that `POST /search-info/search` works fine without `cf_clearance` — so this specific endpoint isn't gated by the challenge, unlike (presumably) the rest of the site. No need for a headless browser for this call.

### Client inconsistency

The endpoint responds with JSON when called via `curl`, but returns Cloudflare's
"Just a moment..." JS challenge page when called via Insomnia — likely due to
TLS/connection-level fingerprinting rather than headers, since headers were
otherwise equivalent. Not yet tested with `java.net.http.HttpClient`; if it also
gets challenged, this needs a real workaround (see Cloudflare section).
