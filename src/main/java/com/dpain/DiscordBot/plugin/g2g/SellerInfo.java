package com.dpain.DiscordBot.plugin.g2g;

public class SellerInfo {
  public String name;
  public double price;

  public SellerInfo(String name, double price) {
    this.name = name;
    this.price = price;
  }
  
  public double getPrice() {
    return price;
  }

  @Override
  public String toString() {
    return String.format("SellerInfo [ name: %s, price: %.6f ]", name, price);
  }
  
  public String toStringEntry() {
    return String.format("Seller: %s - Price: %.6f", name, price);
  }
}
