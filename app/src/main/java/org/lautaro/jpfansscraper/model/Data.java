package org.lautaro.jpfansscraper.model;

import java.util.List;

public class Data {
  protected List<Item> items;
  protected String itemQueryId;
  protected String originalKeyword;
  protected String translatedKeyword;
  protected String channel;
  protected String cache;
  protected String isAuction;

  public Data(List<Item> items, String itemQueryId, String originalKeyword, String translatedKeyword, String channel,
      String cache, String isAuction) {
    this.items = items;
    this.itemQueryId = itemQueryId;
    this.originalKeyword = originalKeyword;
    this.translatedKeyword = translatedKeyword;
    this.channel = channel;
    this.cache = cache;
    this.isAuction = isAuction;
    this.channel = channel;
    this.cache = cache;
    this.isAuction = isAuction;
  }

  @Override
  public String toString() {
    String s = "";
    s += "  items:" + "\n";

    for (Item item : items) {
      s += item.toString();
    }

    s += "  itemQueryId: " + itemQueryId + "\n";
    s += "  originalKeyword: " + originalKeyword + "\n";
    s += "  translatedKeyword: " + translatedKeyword + "\n";
    s += "  channel: " + channel + "\n";
    s += "  cache: " + cache + "\n";
    s += "  isAuction: " + isAuction + "\n";

    return s;
  }
}
