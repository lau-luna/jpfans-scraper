package org.lautaro.jpfansscraper.listing;

public class Item {
  protected String id;
  protected String name;
  protected String nameI18n;
  protected String price;
  protected String image;
  protected String internalCode;

  public Item() {
    id = "";
    name = "";
    nameI18n = "";
    price = "";
    image = "";
    internalCode = "";
  }

  public Item(String id, String name, String nameI18n, String price, String image, String internalCode) {
    this.id = id;
    this.name = name;
    this.nameI18n = nameI18n;
    this.price = price;
    this.image = image;
    this.internalCode = internalCode;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null)
      return false;

    if (!(o instanceof Item))
      return false;

    Item item = (Item) o;

    if (!item.id.equals(this.id))
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    String s = "";
    s += "    id: " + id + "\n";
    s += "    name: " + name + "\n";
    s += "    nameI18n: " + nameI18n + "\n";
    s += "    price: " + price + "\n";
    s += "    image: " + image + "\n";
    s += "    internalCode: " + internalCode + "\n";
    s += "\n";

    return s;
  }
}
