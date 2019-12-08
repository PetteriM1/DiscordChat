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
        if (DiscordChatMain.jda != null) {
            TextChannel channel = DiscordChatMain.jda.getTextChannelById(toConsole ? DiscordChatMain.consoleChannelId : DiscordChatMain.channelId);
            if (channel != null) {
                channel.sendMessage(message).queue();
            } else if (DiscordChatMain.debug) {
                Server.getInstance().getLogger().error("TextChannel is null");
            }
        } else if (DiscordChatMain.debug) {
            Server.getInstance().getLogger().error("JDA is null");
        }
    }

}
