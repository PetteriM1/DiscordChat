package me.petterim1.discordchat;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

public class DiscordChatListener extends ListenerAdapter {

    static final List<DiscordChatReceiver> receivers = new ArrayList<>();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getMember() == null || Loader.jda == null || e.getAuthor().equals(Loader.jda.getSelfUser())) return;
        for (DiscordChatReceiver receiver : receivers) {
            receiver.receive(e);
        }
        if (!e.getChannel().getId().equals(Loader.channelId)) return;
        if (e.getAuthor().isBot() && !Loader.config.getBoolean("allowBotMessages")) return;
        String message = TextFormat.clean(e.getMessage().getContentStripped());
        if (message.isEmpty()) return;
        if (processDiscordCommand(message)) return;
        if (!Loader.config.getBoolean("enableDiscordToMinecraft")) return;
        if (message.contains("ঋ") || message.contains("ༀ") || message.contains("") || message.contains("")) return;
        if (message.length() > Loader.config.getInt("maxMessageLength")) message = message.substring(0, Loader.config.getInt("maxMessageLength"));
        if (Loader.config.getBoolean("spamFilter")) {
            message = message.replaceAll("\\r\\n|\\r|\\n", " ");
        }
        String name = TextFormat.clean(e.getMember().getEffectiveName()).replace("§", "?").replace("%message%", "?");
        String role = "";
        if (getRole(e.getMember()) != null) role = getColoredRole(getRole(e.getMember()));
        String out = Loader.config.getString("discordToMinecraftChatFormatting").replace("%role%", role).replace("%timestamp%", new Date(System.currentTimeMillis()).toString()).replace("%discordname%", name).replace("%message%", message);
        if (Loader.config.getBoolean("enableMessagesToConsole")) {
            Server.getInstance().broadcastMessage(out);
        } else {
            for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                player.sendMessage(out);
            }
        }
    }

     private boolean processDiscordCommand(String m) {
        String prefix = Loader.config.getString("commandPrefix");
        if (Loader.config.getBoolean("playerListCommand") && m.equalsIgnoreCase(prefix + "playerlist")) {
            Map<UUID, Player> playerList = Server.getInstance().getOnlinePlayers();
            if (playerList.isEmpty()) {
                API.sendMessage(Loader.config.getString("command_playerlist_empty"));
            } else {
                String playerListMessage = "";
                playerListMessage += "**" + Loader.config.getString("command_playerlist_players") + " (" + playerList.size() + '/' + Server.getInstance().getMaxPlayers() + "):**";
                playerListMessage += "\n```\n";
                StringJoiner players = new StringJoiner(", ");
                for (Player player : playerList.values()) {
                    players.add(player.getName());
                }
                playerListMessage += players.toString();
                if (playerListMessage.length() > 1996) playerListMessage = playerListMessage.substring(0, 1993) + "...";
                playerListMessage += "\n```";
                API.sendMessage(playerListMessage);
            }
            return true;
        } else if (Loader.config.getBoolean("ipCommand") && m.equalsIgnoreCase(prefix + "ip")) {
            API.sendMessage("```\n" + Loader.config.getString("commands_ip_address") + ' ' + Loader.config.getString("serverIp") + '\n' + Loader.config.getString("commands_ip_port") + ' ' + Loader.config.getString("serverPort") + "\n```");
            return true;
        }
        return false;
    }

    private static Role getRole(Member m) {
        for (Role role : m.getRoles()) {
            return role;
        }
        return null;
    }

    private static String getColoredRole(Role r) {
        if (r == null) return "";
        String hex = r.getColor() != null ? Integer.toHexString(r.getColor().getRGB()).toUpperCase() : "99AAB5";
        if (hex.length() == 8) hex = hex.substring(2);
        return Loader.roleColors.get(hex) + r.getName();
    }
}
