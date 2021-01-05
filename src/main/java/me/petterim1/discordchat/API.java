package me.petterim1.discordchat;

import net.dv8tion.jda.api.entities.TextChannel;
import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.locale.TextContainer;
import org.cloudburstmc.server.locale.TranslationContainer;

public class API {

    public static void sendMessage(String message) {
        if (Loader.jda != null) {
            TextChannel channel = Loader.jda.getTextChannelById(Loader.channelId);
            if (channel != null) {
                channel.sendMessage(message).queue();
            } else if (Loader.debug) {
                Loader.logger.error("TextChannel is null");
            }
        } else if (Loader.debug) {
            Loader.logger.error("JDA is null");
        }
    }

    public static void sendToConsole(String message) {
        if (Loader.jda != null) {
            TextChannel channel = Loader.jda.getTextChannelById(Loader.consoleChannelId);
            if (channel != null) {
                channel.sendMessage(message).queue();
            } else if (Loader.debug) {
                Loader.logger.error("TextChannel for console is null");
            }
        } else if (Loader.debug) {
            Loader.logger.error("JDA is null");
        }
    }

    public static String textFromContainer(TextContainer container) {
        if (container instanceof TranslationContainer) {
            return Server.getInstance().getLanguage().translate(container.getText(), ((TranslationContainer) container).getParameters());
        }
        return container.getText();
    }
}
