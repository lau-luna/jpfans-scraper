# java.net.http.HttpClient Cheatsheet
 
## Client
 
```java
HttpClient client = HttpClient.newHttpClient();
```
 
## GET request
 
```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://example.com"))
    .GET()
    .build();
 
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
 
response.statusCode();
response.body();
```
 
## POST request
 
```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://example.com/endpoint"))
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
    .build();
```
 
## Headers
 
```java
HttpRequest.newBuilder()
    .header("Accept", "application/json")
    .header("User-Agent", "...")
```
