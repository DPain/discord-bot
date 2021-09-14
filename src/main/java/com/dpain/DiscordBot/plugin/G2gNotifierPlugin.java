package com.dpain.DiscordBot.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.G2gServer;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.helper.MessageHelper;
import com.dpain.DiscordBot.plugin.g2g.G2gAlerter;
import com.dpain.DiscordBot.plugin.g2g.SubscriptionInfo;
import com.dpain.DiscordBot.system.PropertiesManager;
import com.dpain.DiscordBot.plugin.g2g.SellerInfo;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class G2gNotifierPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(G2gNotifierPlugin.class);

  public G2gNotifierPlugin(EventWaiter waiter, DiscordBot bot) {
    super("G2gNotifierPlugin", Group.TRUSTED_USER, waiter, bot);
    String g2gInterval = PropertiesManager.load().getValue(Property.G2G_ALERT_INTERVAL);

    try {
      int i = Integer.parseInt(g2gInterval);
      G2gAlerter.load(i);
    } catch (NumberFormatException e) {
      // Could not parse property!
      G2gAlerter.load();
      logger.warn(String.format("Could not parse the property value of key: %s",
          Property.G2G_ALERT_INTERVAL.getKey()));
      logger.warn("Using Default value instead!");
    }
  }

  private String[] getPrices(List<SellerInfo> list) {
    ArrayList<String> output = new ArrayList<String>();

    Iterator<SellerInfo> iter = list.iterator();
    while (iter.hasNext()) {
      SellerInfo sellerInfo = iter.next();

      output.add(sellerInfo.toStringEntry());
    }

    return output.toArray(new String[output.size()]);
  }
  
  @Override
  public void onSlashCommand(SlashCommandEvent event) {
    if (canAccessPlugin(event.getMember())
        && !event.getMember().getUser().getId().equals(event.getJDA().getSelfUser().getId())) {
      String message = String.format("CMD: %s - %s", event.getName(), Arrays.toString(event.getOptions().toArray()));

      // Only accept commands from guilds.
      if (event.getGuild() == null) {
        return;
      }
      switch (event.getName()) {
        case "g2g-add":
          event.deferReply().queue();
          
          String serverName = event.getOption("server-name").getAsString();
          String price = event.getOption("price").getAsString();
          
          G2gServer serverToAdd;
          try {
            serverToAdd = G2gServer.valueOf(serverName.toUpperCase());
          } catch (IllegalArgumentException e) {
            event.getHook().sendMessage("Incorrect Server name!").queue();
            return;
          }

          double priceLimit;
          try {
            priceLimit = Double.parseDouble(price);
          } catch (NumberFormatException e) {
            event.getHook().sendMessage("Incorrect price limit!").queue();
            return;
          }

          SubscriptionInfo info = new SubscriptionInfo();
          info.server = serverToAdd;
          info.limit = priceLimit;

          boolean added = G2gAlerter.load().addUser(event.getUser(), info);
          if (added) {
            event.getHook().sendMessage("Added user to subscription list.").queue();
          } else {
            event.getHook().sendMessage("You were already subscripted.").queue();
          }

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        case "g2g-remove":
          boolean removed = G2gAlerter.load().removeUser(event.getUser());
          if (removed) {
            event.reply("Removed user from subscription list.").queue();
          } else {
            event.reply("You were not subscripted.").queue();
          }

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
        case "g2g-check":
          event.deferReply(true).queue();
          
          User user = event.getUser();
          user.openPrivateChannel().queue((channel) -> {
            try {
              G2gServer serverToCheck = null;
              if (G2gAlerter.load().userExists(user)) {
                serverToCheck = G2gAlerter.load().getSubscriptionInfo(user).server;
              } else {
                serverToCheck = G2gAlerter.DEFAULT_SERVER;
              }

              ArrayList<SellerInfo> prices = G2gAlerter.load().checkPrice(serverToCheck);
              prices.sort((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
              MessageHelper.sendPage(String.format("**%s G2G Gold Prices: **", serverToCheck.toString()),
                  getPrices(prices), 3, 15, waiter, channel, 30, TimeUnit.MINUTES);
              event.getHook().sendMessage("Command Processed!").queue();
            } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          });

          logger.info(LogHelper.elog(event, String.format("Command: %s", message)));
          break;
      }
    }
  }

  @Override
  public void setCommandDescriptions() {
    super.commands.add(new CommandData("g2g-add",
        "Adds to user to the G2G Notifier list for a server with the corresponding rate.")
            .addOption(OptionType.STRING, "server-name", "Name of the server.", true)
            .addOption(OptionType.STRING, "price", "Rates of usd/gold that you want to be notified",
                true));
    super.commands
        .add(new CommandData("g2g-remove", "Removes yourself from the G2G Notifier list."));
    super.commands
        .add(new CommandData("g2g-check", "Checks G2G price and sends a Private message."));
  }
}
