package me.petterim1.discordchat;

import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.utils.TextFormat;

public class DiscordCommandSender extends ConsoleCommandSender {

    @Override
    public void sendMessage(String message) {
        message = TextFormat.clean(message);
        if (!message.isEmpty()) {
            API.sendToConsole(message);
        }
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
