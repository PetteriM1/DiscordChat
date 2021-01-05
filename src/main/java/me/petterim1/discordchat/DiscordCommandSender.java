package me.petterim1.discordchat;

import org.cloudburstmc.server.command.ConsoleCommandSender;
import org.cloudburstmc.server.locale.TextContainer;
import org.cloudburstmc.server.utils.TextFormat;

public class DiscordCommandSender extends ConsoleCommandSender {

    @Override
    public void sendMessage(String message) {
        message = TextFormat.clean(message);
        if (message.isEmpty()) return;
        API.sendToConsole(message);
    }

    @Override
    public void sendMessage(TextContainer message) {
        sendMessage(getServer().getLanguage().translate(message));
    }

    @Override
    public String getName() {
        return "DiscordConsoleSender";
    }
}
