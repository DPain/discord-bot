package com.dpain.DiscordBot.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.helper.MessageHelper;
import com.dpain.DiscordBot.plugin.g2g.G2gAlerter;
import com.dpain.DiscordBot.plugin.g2g.SellerInfo;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class G2gNotifierPlugin extends Plugin {
  private final static Logger logger = LoggerFactory.getLogger(G2gNotifierPlugin.class);

  public G2gNotifierPlugin(EventWaiter waiter) {
    super("G2gNotifierPlugin", Group.USER, waiter);
    G2gAlerter.load();
    super.helpString =
        "**G2gNotifier Plugin Usage:** \n-g2g : Gets added to the G2G Notifier list.\n"
            + "-g2gcheck : Checks G2G price and sends a Private message.\n";
    EssentialsPlugin.appendHelpString(super.helpString);
  }

  @Override
  public void handleEvent(Event event) {
    if (event instanceof GuildMessageReceivedEvent) {
      try {
        GuildMessageReceivedEvent castedEvent = (GuildMessageReceivedEvent) event;
        String message = castedEvent.getMessage().getContentRaw();

        if ((castedEvent.getAuthor().getId().equals(event.getJDA().getSelfUser().getId()))
            || canAccessPlugin(castedEvent.getMember())) {
          if (message.equals("-g2g")) {
            boolean result = G2gAlerter.load().toggleUser(castedEvent.getAuthor());
            if (result) {
              castedEvent.getChannel().sendMessage("Added user from subscription list.").queue();
            } else {
              castedEvent.getChannel().sendMessage("Removed user from subscription list.").queue();
            }

          } else if (message.equals("-g2gcheck")) {
            castedEvent.getAuthor().openPrivateChannel().queue((channel) -> {
              try {
                ArrayList<SellerInfo> prices = G2gAlerter.load().checkPrice();
                prices.sort((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
                MessageHelper.sendPage("**G2G Gold Prices: **", getPrices(prices), 3, 15, waiter, channel, 30, TimeUnit.MINUTES);
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
}
