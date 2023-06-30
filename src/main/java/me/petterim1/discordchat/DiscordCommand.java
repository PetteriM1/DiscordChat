package me.petterim1.discordchat;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class DiscordCommand {

    static boolean handleCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Loader.config.getBoolean("discordCommand") && command.getName().equals("discord")) {
            if (args.length > 1) {
                return false;
            } else if (args.length == 1) {
                String cmd = args[0].toLowerCase();
                if (cmd.equals("mute") && sender instanceof Player) {
                    if (DiscordListener.chatMuted.contains(sender.getName())) {
                        sender.sendMessage(Loader.config.getString("command_mute_already_muted"));
                    } else {
                        DiscordListener.chatMuted.add(sender.getName());
                        sender.sendMessage(Loader.config.getString("command_mute_success"));
                    }
                    return true;
                } else if (cmd.equals("unmute") && sender instanceof Player) {
                    if (DiscordListener.chatMuted.remove(sender.getName())) {
                        sender.sendMessage(Loader.config.getString("command_unmute_success"));
                    } else {
                        sender.sendMessage(Loader.config.getString("command_unmute_not_muted"));
                    }
                    return true;
                } else if (cmd.equals("reload")) {
                    if (sender.isOp()) {
                        try {
                            if (Loader.jda != null) {
                                if (Loader.debug) {
                                    Loader.instance.getLogger().info("Shutting down old JDA instance");
                                }
                                Loader.jda.shutdownNow();
                            }
                            Loader.instance.onEnable();
                            sender.sendMessage("§aDiscordChat reloaded");
                        } catch (Exception ex) {
                            sender.sendMessage("§cAn error occurred");
                            Server.getInstance().getLogger().logException(ex);
                        }
                    } else {
                        sender.sendMessage(Loader.config.getString("command_generic_no_perm"));
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
