package me.petterim1.discordchat;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.utils.TextFormat;

import java.util.Date;
import java.util.StringJoiner;

public class DiscordChatListener extends ListenerAdapter {

    private static String lastMessage;
    private static String lastName;

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor() == null || e.getMember() == null || e.getAuthor().getId() == null || Loader.jda == null || Loader.jda.getSelfUser() == null || Loader.jda.getSelfUser().getId() == null || e.getAuthor().equals(Loader.jda.getSelfUser())) return;
        if (!e.getChannel().getId().equals(Loader.channelId)) return;
        if (e.getAuthor().isBot() && !Loader.config.getBoolean("allowBotMessages")) return;
        String message = TextFormat.clean(e.getMessage().getContentStripped());
        if (message.isEmpty()) return;
        if (processPlayerListCommand(message)) return;
        if (!Loader.config.getBoolean("enableDiscordToMinecraft")) return;
        if (message.contains("ঋ") || message.contains("ༀ") || message.contains("") || message.contains("")) return;
        if (message.length() > Loader.config.getInt("maxMessageLength")) message = message.substring(0, Loader.config.getInt("maxMessageLength"));
        String name = TextFormat.clean(e.getMember().getEffectiveName()).replace("§", "?").replace("%message%", "?");
        if (Loader.config.getBoolean("spamFilter")) {
            if (message.equals(lastMessage) && name.equals(lastName)) return;
            lastMessage = message;
            lastName = name;
            message = message.replaceAll("\\r\\n|\\r|\\n", " ");
        }
        String role = "";
        if (getRole(e.getMember()) != null) role = getRole(getRole(e.getMember()));
        String out = Loader.config.getString("discordToMinecraftChatFormatting").replace("%role%", role).replace("%timestamp%", new Date(System.currentTimeMillis()).toString()).replace("%discordname%", name).replace("%message%", message);
        if (Loader.config.getBoolean("enableMessagesToConsole")) {
            Server.getInstance().broadcastMessage(out);
        } else {
            for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                player.sendMessage(out);
            }
        }
    }

     private boolean processPlayerListCommand(String message) {
        if (message.equalsIgnoreCase(Loader.config.getString("commandPrefix") + "playerlist") && Loader.config.getBoolean("playerListCommand")) {
            if (Server.getInstance().getOnlinePlayers().isEmpty()) {
                API.sendMessage(Loader.config.getString("command_playerlist_empty"));
            } else {
                String playerListMessage = "";
                playerListMessage += "**" + Loader.config.getString("command_playerlist_players") + " (" + Server.getInstance().getOnlinePlayers().size() + '/' + Server.getInstance().getMaxPlayers() + "):**";
                playerListMessage += "\n```\n";
                StringJoiner players = new StringJoiner(", ");
                for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                    players.add(player.getName());
                }
                playerListMessage += players.toString();
                if (playerListMessage.length() > 1996) playerListMessage = playerListMessage.substring(0, 1993) + "...";
                playerListMessage += "\n```";
                API.sendMessage(playerListMessage);
            }
            return true;
        } else if (message.equalsIgnoreCase(Loader.config.getString("commandPrefix") + "ip") && Loader.config.getBoolean("ipCommand")) {
            API.sendMessage("```\n" + Loader.config.getString("commands_ip_address") + ' ' + Loader.config.getString("serverIp") + '\n' + Loader.config.getString("commands_ip_port") + ' ' + Loader.config.getString("serverPort") + "\n```");
            return true;
        }
        return false;
    }

    private Role getRole(Member m) {
        for (Role role : m.getRoles()) {
            return role;
        }
        return null;
    }

    private String getRole(Role role) {
        if (role == null) return "";
        String hex = role.getColor() != null ? Integer.toHexString(role.getColor().getRGB()).toUpperCase() : "99AAB5";
        if (hex.length() == 8) hex = hex.substring(2);
        return Loader.roleColors.get(hex) + role.getName();
    }
}
