package com.dpain.DiscordBot.plugin.g2g;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import com.dpain.DiscordBot.enums.G2gServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

public class G2gAlerter {
  private final static Logger logger = LoggerFactory.getLogger(G2gAlerter.class);

  private final String SUBSCRIPTION_FILENAME = "subscription.yml";
  private Map<String, SubscriptionInfo> userMap = new HashMap<String, SubscriptionInfo>();
  
  public final static G2gServer DEFAULT_SERVER = G2gServer.KADUM;

  private JDA jda;

  private static G2gAlerter ref;

  /**
   * Constructor
   */
  private G2gAlerter() {
    try {
      readSubscriptionFile();

      // Calculating initial delay
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime nextRun = now.withMinute(0).withSecond(0).plusHours(1);

      Duration duration = Duration.between(now, nextRun);
      long initalDelay = duration.getSeconds();

      // Starting scheduled service every hour
      ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
      scheduler.scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {
          for (G2gServer server : G2gServer.values()) {
            broadcastPrice(server);
          }
        }
      }, initalDelay, TimeUnit.HOURS.toSeconds(1), TimeUnit.SECONDS);

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

  /**
   * Sets the JDA the Alerter will use
   * 
   * @param jda
   */
  public void setJDA(JDA jda) {
    this.jda = jda;
  }

  /**
   * Gets the JDA that Alerter is using
   * 
   * @return jda
   */
  public JDA getJDA() {
    return jda;
  }

  /**
   * Reads the list of users who signed for alert subscription.
   * 
   * @throws IOException
   */
  private void readSubscriptionFile() throws IOException {
    CustomClassLoaderConstructor constructor =
        new CustomClassLoaderConstructor(YamlSubscriptionFormat.class.getClassLoader());
    constructor.addTypeDescription(new TypeDescription(YamlSubscriptionFormat.class, "!subscriptions"));
    constructor.addTypeDescription(new TypeDescription(SubscriptionInfo.class, "!info"));
    constructor
        .addTypeDescription(new TypeDescription(G2gServer.class, "!server"));
    Yaml yaml = new Yaml(constructor);

    try {
      YamlSubscriptionFormat container =
          (YamlSubscriptionFormat) yaml.load(new FileReader(SUBSCRIPTION_FILENAME));
      userMap = container.subscriptions;
    } catch (FileNotFoundException | NullPointerException | YAMLException | ClassCastException e) {
      rebuild();
      logger.info("Error occured. Generated a new subscription file!");
    }
  }

  /**
   * Saves subscription list from memory to file.
   */
  public void saveConfig() {
    Representer representer = new Representer();
    representer.addClassTag(YamlSubscriptionFormat.class, new Tag("!subscriptions"));
    representer.addClassTag(SubscriptionInfo.class, new Tag("!info"));
    representer.addClassTag(G2gServer.class, new Tag("!server"));
    Yaml yaml = new Yaml(representer);

    try {
      YamlSubscriptionFormat container = new YamlSubscriptionFormat();
      container.subscriptions = userMap;
      
      yaml.dump(container, new FileWriter(SUBSCRIPTION_FILENAME));
    } catch (IOException e) {
      logger.error("Failed to read the config file!");
    }
  }
  
  /**
   * Returns whether the user is subscripted or not.
   * @param user
   * @return boolean
   */
  public boolean userExists(User user) {
    return userMap.containsKey(user.getId());
  }
  
  /**
   * Gets the subscription information of a user.
   * @param user
   * @return SubscriptionInfo
   */
  public SubscriptionInfo getSubscriptionInfo(User user) {
    return userMap.get(user.getId());
  }

  /**
   * Adds a user to the userMap.
   * 
   * @param user
   * @return true when added, false when user already exists.
   */
  public boolean addUser(User user, SubscriptionInfo info) {
    String key = user.getId();
    if (!userMap.containsKey(key)) {
      userMap.put(key, info);
      logger.info("User subscripted to G2gAlerter: " + key);

      saveConfig();
      return true;
    }
    return false;
  }

  /**
   * Removes a user from the userMap.
   * 
   * @param user
   * @return true when remove, false when user did not exist.
   */
  public boolean removeUser(User user) {
    String key = user.getId();
    if (userMap.containsKey(key)) {
      userMap.remove(key);
      logger.info("User unsubscripted to G2gAlerter: " + key);

      saveConfig();
      return true;
    }
    return false;
  }

  /**
   * Removes everyone in the subscription list.
   */
  public void rebuild() {
    // Clears the userMap
    userMap.clear();

    logger.info("Rebuit " + SUBSCRIPTION_FILENAME + " file!");
    saveConfig();
  }

  /**
   * Reads the subscription list into memory again.
   */
  public void reload() {
    try {
      readSubscriptionFile();
    } catch (IOException e) {
      logger.error("Did not have permission to read the " + SUBSCRIPTION_FILENAME + " file!");
    }
  }

  /**
   * Checks price from G2G and returns the lowest price if it is lower than the threshold.
   */
  public void broadcastPrice(G2gServer server) {
    try {
      ArrayList<SellerInfo> list = G2gAlerter.load().checkPrice(server);
      SellerInfo min = list.stream().min(Comparator.comparing(SellerInfo::getPrice))
          .orElseThrow(NoSuchElementException::new);

      logger.info(String.format("Broadcasting G2G Seller Info:\n", min));
      
      for (String id : userMap.keySet()) {
        if (server == userMap.get(id).server && min.getPrice() <= userMap.get(id).limit) {
          jda.getUserById(id).openPrivateChannel().queue((channel) -> {
            channel.sendMessage(min.toString()).queue();
          });
        }
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Checks price from G2G.
   * 
   * @return ArrayList<SellerInfo> list of sellers
   * @throws IOException
   */
  public ArrayList<SellerInfo> checkPrice(G2gServer server) throws IOException {
    ArrayList<SellerInfo> result = new ArrayList<SellerInfo>();
    String parseLink = "https://www.g2g.com/archeage-us/Gold-20354-20357?&server=" + server.getID();
    Document doc = Jsoup.connect(parseLink).get();
    
    Element container = doc.select("ul.products__list").first();
    Elements listing = container.select("li.products__list-item");
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
