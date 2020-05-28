package com.dpain.DiscordBot.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.DiscordBot;
import com.dpain.DiscordBot.enums.G2gServer;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.helper.MessageHelper;
import com.dpain.DiscordBot.plugin.g2g.G2gAlerter;
import com.dpain.DiscordBot.plugin.g2g.SubscriptionInfo;
import com.dpain.DiscordBot.system.PropertiesManager;
import com.dpain.DiscordBot.plugin.g2g.SellerInfo;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

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

  @Override
  public void handleEvent(GenericEvent event) {
    if (event instanceof GuildMessageReceivedEvent) {
      try {
        GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
        String message = castedEvent.getMessage().getContentRaw();

        if ((castedEvent.getAuthor().getId().equals(event.getJDA().getSelfUser().getId()))
            || canAccessPlugin(castedEvent.getMember())) {
          if (message.startsWith("-g2gadd ")) {
            String[] params = message.substring("-g2gadd ".length()).split(" ");
            if (params.length >= 2) {
              G2gServer server;
              try {
                server = G2gServer.valueOf(params[0].toUpperCase());
              } catch (IllegalArgumentException e) {
                castedEvent.getChannel().sendMessage("Incorrect Server name!").queue();
                return;
              }

              double priceLimit;
              try {
                priceLimit = Double.parseDouble(params[1]);
              } catch (NumberFormatException e) {
                castedEvent.getChannel().sendMessage("Incorrect price limit!").queue();
                return;
              }

              SubscriptionInfo info = new SubscriptionInfo();
              info.server = server;
              info.limit = priceLimit;

              boolean result = G2gAlerter.load().addUser(castedEvent.getAuthor(), info);
              if (result) {
                castedEvent.getChannel().sendMessage("Added user to subscription list.").queue();
              } else {
                castedEvent.getChannel().sendMessage("You were already subscripted.").queue();
              }
            } else {
              castedEvent.getChannel().sendMessage("Incorrect amount of parameters!").queue();
            }
          } else if (message.equals("-g2gremove")) {
            boolean result = G2gAlerter.load().removeUser(castedEvent.getAuthor());
            if (result) {
              castedEvent.getChannel().sendMessage("Removed user from subscription list.").queue();
            } else {
              castedEvent.getChannel().sendMessage("You were not subscripted.").queue();
            }
          } else if (message.equals("-g2gcheck")) {
            User user = castedEvent.getAuthor();
            user.openPrivateChannel().queue((channel) -> {
              try {
                G2gServer server = null;
                if (G2gAlerter.load().userExists(user)) {
                  server = G2gAlerter.load().getSubscriptionInfo(user).server;
                } else {
                  server = G2gAlerter.DEFAULT_SERVER;
                }

                System.out.println(server);

                ArrayList<SellerInfo> prices = G2gAlerter.load().checkPrice(server);
                prices.sort((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
                MessageHelper.sendPage(String.format("**%s G2G Gold Prices: **", server.toString()),
                    getPrices(prices), 3, 15, waiter, channel, 30, TimeUnit.MINUTES);
              } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            });
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
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
  public void setCommandDescriptions() {
    super.commands.put("-g2gadd *\\\"serverName\\\"* *\\\"price\\\"*",
        "Gets added to the G2G Notifier list for a server with the corresponding rate.");
    super.commands.put("-g2gremove", "Removes yourself from the G2G Notifier list.");
    super.commands.put("-g2gcheck", "Checks G2G price and sends a Private message.");
  }
}
