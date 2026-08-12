package org.lautaro.jpfansscraper;

import org.lautaro.jpfansscraper.api.JPFansClient;

public class Main {
  public static void main(String[] args) {
    JPFansClient client = new JPFansClient();
    try {
      client.postRequest();
    } catch (Exception e) {
      System.out.println(e.toString());
    }
  }
}
