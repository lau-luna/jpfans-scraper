# Jsoup Cheatsheet

## Fetch / Parse

```java
// From URL (with options)
Document doc = Jsoup.connect("https://example.com/search?q=something")
    .userAgent("Mozilla/5.0")
    .timeout(10_000)
    .header("Accept-Language", "en-US")
    .cookie("session", "value")     // if you need to send cookies
    .get();                          // or .post()

// From a local file (for tests)
Document doc = Jsoup.parse(new File("path.html"), "UTF-8");

// From a String already in memory
Document doc = Jsoup.parse(htmlString);

// From InputStream with base URI (to resolve relative links)
Document doc = Jsoup.parse(inputStream, "UTF-8", "https://example.com/");
```

## Selecting elements (CSS-style selectors)

```java
Elements items = doc.select(".item-card");           // by class
Elements items = doc.select("#results");               // by id
Elements items = doc.select("div.item > a");             // direct child
Elements items = doc.select("a[href]");                    // has attribute
Elements items = doc.select("a[href^=/item/]");              // attribute starts with
Elements items = doc.select("span:contains(psp)");             // by text content
Elements items = doc.select("div.item:eq(0)");                   // by index
Element first = doc.selectFirst(".item-card");                     // just the first match

// Navigate from an already-selected element
Element item = items.first();
Element title = item.selectFirst(".title");
Elements children = item.children();
Element parent = item.parent();
Elements siblings = item.siblingElements();
```

## Extracting data

```java
String text = element.text();               // visible text, no tags
String html = element.html();                 // inner HTML
String outer = element.outerHtml();             // HTML including the tag itself
String href = element.attr("href");               // attribute value
String hrefAbs = element.attr("abs:href");           // attribute resolved to absolute URL
boolean has = element.hasAttr("data-id");
String cls = element.className();
List<String> classes = new ArrayList<>(element.classNames());
```

```

## Cleaning / manipulation (less common for scraping, more for sanitizing)

```java
String clean = Jsoup.clean(dirtyHtml, Safelist.basic());
element.remove();          // remove a node from the tree
element.text("new text");
```

## Typical error handling

```java
try {
    Document doc = Jsoup.connect(url).get();
} catch (HttpStatusException e) {
    // 404, 403, etc. — e.getStatusCode()
} catch (SocketTimeoutException e) {
    // timeout
} catch (IOException e) {
    // generic network/parsing error
}
```

## Practical tips

- `.select()` always returns `Elements` (can be empty, never null) — no need to null-check, just `.isEmpty()`.
- `attr("abs:href")` resolves relative links to absolute ones automatically using the document's base URI — very useful so you don't have to build the full URL yourself.
- For quick selector debugging, `System.out.println(doc.select("...").outerHtml())` prints the matched fragment, to confirm the selector is correct before extracting text/attributes.
