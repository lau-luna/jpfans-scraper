package org.lautaro.jpfansscraper;

import java.util.List;
import java.util.LinkedList;

import org.lautaro.jpfansscraper.api.JPFansClient;
import org.lautaro.jpfansscraper.model.Item;

public class Main {
  public static void main(String[] args) {
    JPFansClient client = new JPFansClient();

    List<Item> items = new LinkedList<>();

    try {
      items = client.searchItems("project diva psp", 0, 0, 5, false);
    } catch (Exception e) {
      System.out.println(e.toString());
    }

    for (Item item : items) {
      // System.out.println(item);
    }
  }
}
