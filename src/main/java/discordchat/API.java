package discordchat;

import cn.nukkit.Server;
import net.dv8tion.jda.api.entities.TextChannel;

public class API {

    public static void sendMessage(String message) {
        API.sendMessage(message, false);
    }

    public static void sendToConsole(String message) {
        API.sendMessage(message, true);
    }

    public static void sendMessage(String message, boolean toConsole) {
        if (Main.jda != null) {
            TextChannel channel = Main.jda.getTextChannelById(toConsole ? Main.consoleChannelId : Main.channelId);
            if (channel != null) {
                channel.sendMessage(message).queue();
            } else if (Main.debug) {
                Server.getInstance().getLogger().error("TextChannel is null");
            }
        } else if (Main.debug) {
            Server.getInstance().getLogger().error("JDA is null");
        }
    }

}
