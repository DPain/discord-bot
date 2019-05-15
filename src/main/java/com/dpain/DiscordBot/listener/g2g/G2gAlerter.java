package com.dpain.DiscordBot.listener.g2g;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import net.dv8tion.jda.core.entities.User;

public class G2gAlerter {
  private final static Logger logger = LoggerFactory.getLogger(G2gAlerter.class);

  // Max message length is 2000. So charLimit must be at max: 1996
  public static int charLimit = 800;
  public static double priceLimit = 0.003;

  private final String SUBSCRIPTION_FILENAME = "subscription.yml";
  private List<String> userList = new ArrayList<String>();

  private static G2gAlerter ref;

  private G2gAlerter() {
    try {
      readSubscriptionFile();
    } catch (IOException e) {
      logger.error("Did not have permission to create the " + SUBSCRIPTION_FILENAME + " file!");
    }
  }

  public static G2gAlerter load() {
    return loadG2gAlerter();
  }

  public static G2gAlerter loadG2gAlerter() {
    if (ref == null) {
      ref = new G2gAlerter();
    }
    return ref;
  }


  private void readSubscriptionFile() throws IOException {
    Yaml yaml = new Yaml();

    try {
      userList = yaml.load(new FileReader(SUBSCRIPTION_FILENAME));
    } catch (FileNotFoundException | NullPointerException | YAMLException e) {
      rebuild();
      logger.info("Error occured. Generated a new subscription file!");
    }
  }

  /**
   * Either adds or removes a user from the subscriptionList.
   * @param user
   * @return true when added, false when removed.
   */
  public boolean toggleUser(User user) {
    if (userList.contains(user.getId())) {
      userList.remove(user.getId());
      logger.info("User id removed: " + user.getId());
      return false;
    } else {
      userList.add(user.getId());
      logger.info("User id added: " + user.getId());
      return true;
    }
  }

  public void rebuild() {
    // Clears the userMap
    userList.clear();

    logger.info("Rebuit " + SUBSCRIPTION_FILENAME + " file!");
    saveConfig();
  }

  public void saveConfig() {
    Yaml yaml = new Yaml();

    try {
      yaml.dump(userList, new FileWriter(SUBSCRIPTION_FILENAME));
    } catch (IOException e) {
      logger.error("Failed to read the config file!");
    }
  }

  public void reload() {
    try {
      readSubscriptionFile();
    } catch (IOException e) {
      logger.error("Did not have permission to create the " + SUBSCRIPTION_FILENAME + " file!");
    }
  }

  public void broadcastPrice() {
    for (String id : userList) {

    }
  }

  public ArrayList<SellerInfo> checkPrice() throws IOException {
    ArrayList<SellerInfo> result = new ArrayList<SellerInfo>();
    String parseLink = "https://www.g2g.com/archeage-us/Gold-20354-20357?&server=29358";
    Document doc = Jsoup.connect(parseLink).get();

    Elements listing = doc.select("li[data-name='ArcheAge (US) > Kadum (Gold)']");
    System.out.println(String.format("listing size: %d", listing.size()));
    for (Element entry : listing) {
      String sellerName = entry.select("a.seller__name").html();
      Element priceSpan = entry.select("span.products__exch-rate").first();
      double price = Double.parseDouble(priceSpan.child(0).html());

      SellerInfo info = new SellerInfo(sellerName, price);
      result.add(info);
    }

    return result;
  }
}
