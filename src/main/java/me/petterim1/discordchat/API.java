package me.petterim1.discordchat;

import cn.nukkit.Server;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import net.dv8tion.jda.api.entities.TextChannel;

public class API {

    public static void sendMessage(String message) {
        sendMessage(Loader.channelId, message);
    }

    public static void sendMessage(String channelId, String message) {
        if (Loader.jda != null) {
            TextChannel channel = Loader.jda.getTextChannelById(channelId);
            if (channel != null) {
                channel.sendMessage(message).queue();
            } else if (Loader.debug) {
                Server.getInstance().getLogger().error("TextChannel is null: " + channelId);
            }
        } else if (Loader.debug) {
            Server.getInstance().getLogger().error("JDA is null");
        }
    }

    public static void sendToConsole(String message) {
        if (Loader.jda != null) {
            TextChannel channel = Loader.jda.getTextChannelById(Loader.consoleChannelId);
            if (channel != null) {
                channel.sendMessage(message).queue();
            } else if (Loader.debug) {
                Server.getInstance().getLogger().error("TextChannel for console is null: " + Loader.consoleChannelId);
            }
        } else if (Loader.debug) {
            Server.getInstance().getLogger().error("JDA is null");
        }
    }

    public static String textFromContainer(TextContainer container) {
        if (container instanceof TranslationContainer) {
            return Server.getInstance().getLanguage().translateString(container.getText(), ((TranslationContainer) container).getParameters());
        }
        return container.getText();
    }
}
