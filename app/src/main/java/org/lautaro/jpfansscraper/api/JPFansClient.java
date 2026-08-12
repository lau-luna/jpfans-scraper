package org.lautaro.jpfansscraper.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;

public class JPFansClient {
  protected HttpClient client;

  public JPFansClient() {
    client = HttpClient.newHttpClient();
  }

  public HttpRequest postRequest(String uri, String jsonBody) {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(uri))
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:153.0) Gecko/20100101 Firefox/153.0")
        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
        .build();

    return request;
  }

  public void postRequest() throws IOException, InterruptedException {
    String uri = "https://jpfans.com/search-info/search?lang=en&language=en&wmc-currency=USD";

    String jsonBody = """
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
        """;

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(uri))
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:153.0) Gecko/20100101 Firefox/153.0")
        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
        .build();

    
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    System.out.println(response.statusCode());
    System.out.println(response.body());
    
    // Gson gson = new Gson();
    // Object response = gson.fromJson(jsonResponse, Object.class);
  }
}
