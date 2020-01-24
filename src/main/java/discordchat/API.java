package discordchat;

import net.dv8tion.jda.api.entities.TextChannel;

public class API {

    public static void sendMessage(String message) {
        if (Main.jda != null) {
            TextChannel channel = Main.jda.getTextChannelById(Main.channelId);
            if (channel != null) {
                channel.sendMessage(message).queue();
            } else if (Main.debug) {
                Main.instance.getLogger().error("TextChannel is null");
            }
        } else if (Main.debug) {
            Main.instance.getLogger().error("JDA is null");
        }
    }

    public static void sendToConsole(String message) {
        if (Main.jda != null) {
            TextChannel channel = Main.jda.getTextChannelById(Main.consoleChannelId);
            if (channel != null) {
                channel.sendMessage(message).queue();
            } else if (Main.debug) {
                Main.instance.getLogger().error("TextChannel for console is null");
            }
        } else if (Main.debug) {
            Main.instance.getLogger().error("JDA is null");
        }
    }
}
