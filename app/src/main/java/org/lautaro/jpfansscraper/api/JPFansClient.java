package org.lautaro.jpfansscraper.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lautaro.jpfansscraper.model.Item;
import org.lautaro.jpfansscraper.model.SearchResponse;

import com.google.gson.Gson;

public class JPFansClient {
  protected HttpClient client;

  public JPFansClient() {
    client = HttpClient.newHttpClient();
  }

  // TODO: make it a private method, only public now for testing
  public SearchResponse postSearch(String keyword, int priceMin, int priceMax, int page, int sort,
      boolean translateKeywords) throws IOException, InterruptedException {
    String uri = "https://jpfans.com/search-info/search?lang=en&language=en&wmc-currency=USD";

    String jsonBody = """
        {
          "platform": "mercari",
          "cacheDisabled": false,
          "category": [],
          "keyword": "%s",
          "excludeKeyword": "",
          "itemTypes": [],
          "brands": [],
          "productCondition": [],
          "sizes": [],
          "auctionOptions": "",
          "priceOptions": "",
          "priceMin": %d,
          "priceMax": %d,
          "shippingCost": [],
          "colors": [],
          "page": %d,
          "pageSize": 100,
          "sort": "%d",
          "shopId": "",
          "userId": "",
          "translateKeywords": %b,
          "lang": "en",
          "language": "en",
          "site": "jp"
        }
        """.formatted(keyword, priceMin, priceMax, page, sort, translateKeywords);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(uri))
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:153.0) Gecko/20100101 Firefox/153.0")
        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    // System.out.println(response);

    // System.out.println(response.statusCode());
    // System.out.println(response.body());

    Gson gson = new Gson();
    SearchResponse searchResponse = gson.fromJson(response.body(), SearchResponse.class);

    // System.out.println(searchResponse);

    return searchResponse;
  }

  public List<Item> searchItems(String keyword, int priceMin, int priceMax, int sort, boolean translateKeywords)
      throws IOException, InterruptedException {

    int page = 0;
    boolean reachedEnd = false;

    Map<String, Item> uniqueItems = new HashMap<>();

    int totalFetchedItems = 0;
    int repeatedCount = 0;

    do {
      SearchResponse searchResponse = postSearch(keyword, priceMin, priceMax, page++, sort, translateKeywords);

      System.out.println("page" + page);

      List<Item> fetched = searchResponse.getItems();

      for (Item item : fetched) {
        if (uniqueItems.put(item.getId(), item) != null) {
          repeatedCount++;
        }
      }

      totalFetchedItems += fetched.size();

      reachedEnd = searchResponse.getItems().isEmpty();
    } while (!reachedEnd);

    System.out.println("Total Fetched items: " + totalFetchedItems);
    System.out.println("Unique ids: " + uniqueItems.size());
    System.out.println("Repeated items: " + repeatedCount);
    System.out.println("Sort type: " + sort);

    return new LinkedList<>(uniqueItems.values());
  }
}
