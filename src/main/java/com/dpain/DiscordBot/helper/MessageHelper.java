package com.dpain.DiscordBot.helper;

import java.util.concurrent.TimeUnit;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;

public class MessageHelper {
  
  public static void sendPage(final String title, final String[] items, int numCol, int numItems, EventWaiter waiter, MessageChannel channel, long time, TimeUnit timeUnit) {
    Paginator pBuilder = new Paginator.Builder().setColumns(numCol).setItemsPerPage(numItems)
        .showPageNumbers(true)
        .waitOnSinglePage(false)
        .useNumberedItems(true)
        .setFinalAction(m -> {
            try {
                m.clearReactions().queue();
            } catch(PermissionException ex) {
                m.delete().queue();
            }
        }).setText(title)
        .setItems(items)
        .setEventWaiter(waiter)
        .setTimeout(time, timeUnit).build();
    pBuilder.display(channel);
  }
}
