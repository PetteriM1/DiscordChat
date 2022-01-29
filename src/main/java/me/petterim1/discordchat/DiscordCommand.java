package me.petterim1.discordchat;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class DiscordCommand {

    static boolean handleCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && Loader.config.getBoolean("discordCommand") && command.getName().equals("discord")) {
            if (args.length > 1) {
                return false;
            } else if (args.length == 1) {
                String cmd = args[0].toLowerCase();
                if (cmd.equals("mute")) {
                    if (DiscordChatListener.chatMuted.contains(sender.getName())) {
                        sender.sendMessage(Loader.config.getString("command_mute_already_muted"));
                    } else {
                        DiscordChatListener.chatMuted.add(sender.getName());
                        sender.sendMessage(Loader.config.getString("command_mute_success"));
                    }
                    return true;
                } else if (cmd.equals("unmute")) {
                    if (DiscordChatListener.chatMuted.remove(sender.getName())) {
                        sender.sendMessage(Loader.config.getString("command_unmute_success"));
                    } else {
                        sender.sendMessage(Loader.config.getString("command_unmute_not_muted"));
                    }
                    return true;
                } else {
                    return false;
                }
            }
            sender.sendMessage(Loader.config.getString("discordCommandOutput"));
            return true;
        }
        return false;
    }
}
