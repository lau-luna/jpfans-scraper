# Gson Cheatsheet
 
## Setup
 
```groovy
implementation 'com.google.code.gson:gson:2.11.0'
```
 
## JSON → Java
 
```java
Gson gson = new Gson();
Item item = gson.fromJson(jsonString, Item.class);
```
 
## Java → JSON
 
```java
String json = gson.toJson(item);
```
 
