package org.lautaro.jpfansscraper.model;

import java.util.List;

public class SearchResponse {
  protected int code;
  protected Data data;
  protected String msg;

  public SearchResponse(int code, Data data, String msg) {
    this.code = code;
    this.data = data;
    this.msg = msg;
  }
  
  /**
   * Returns the item for the response
   */
  public List<Item> getItems() {
    return data.getItems();
  }

  @Override
  public String toString() {
    String s = "";

    s += "code: " + code + "\n";
    s += "data: " + "\n";
    s += data.toString();
    s += "msg: " + msg + "\n";

    return s;
  }
}
