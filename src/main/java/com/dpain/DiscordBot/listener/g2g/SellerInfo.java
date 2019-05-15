package com.dpain.DiscordBot.listener.g2g;

public class SellerInfo {
  public String name;
  public double price;

  public SellerInfo(String name, double price) {
    this.name = name;
    this.price = price;
  }

  @Override
  public String toString() {
    return String.format("SellerInfo [ name: %s, price: %.6f ]", name, price);
  }
}
