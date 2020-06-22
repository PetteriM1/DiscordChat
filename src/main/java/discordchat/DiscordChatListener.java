package discordchat;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.StringJoiner;

public class DiscordChatListener extends ListenerAdapter {

    private String lastMessage;
    private String lastName;

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor() == null || e.getMember() == null || e.getAuthor().getId() == null || Main.jda == null || Main.jda.getSelfUser() == null || Main.jda.getSelfUser().getId() == null || e.getAuthor().equals(Main.jda.getSelfUser())) return;
        if (!e.getChannel().getId().equals(Main.channelId)) return;
        if (e.getAuthor().isBot() && !Main.config.getBoolean("allowBotMessages")) return;
        String message = TextFormat.clean(e.getMessage().getContentStripped());
        if (message.isEmpty() && e.getMessage().getAttachments().isEmpty()) return;
        if (processPlayerListCommand(message)) return;
        if (message.contains("ঋ") || message.contains("ༀ") || message.contains("") || message.contains("")) return;
        if (message.length() > Main.config.getInt("maxMessageLength")) message = message.substring(0, Main.config.getInt("maxMessageLength"));
        String role = "";
        if (getRole(e.getMember()) != null) role = " \u00A7f| " + getRole(getRole(e.getMember()));
        if (!Main.config.getBoolean("enableDiscordToMinecraft")) return;
        String name = e.getMember().getEffectiveName().replace("§", "");
        if (Main.config.getBoolean("spamFilter")) {
            if (message.equals(lastMessage) && name.equals(lastName)) return;
            lastMessage = message;
            lastName = name;
            message = message.replaceAll("\\r\\n|\\r|\\n", " ");
        }
        if (Main.config.getBoolean("enableMessagesToConsole")) {
            Server.getInstance().broadcastMessage(Main.config.getString("discord_prefix").replace("%role%", role) + ' ' + name + " \u00BB " + message);
        } else {
            for (Player player : Server.getInstance().getOnlinePlayers().values()) player.sendMessage(Main.config.getString("discord_prefix").replace("%role%", role) + ' ' + name + " \u00BB " + message);
        }
    }

     private boolean processPlayerListCommand(String message) {
        if (message.equalsIgnoreCase(Main.config.getString("commandPrefix") + "playerlist") && Main.config.getBoolean("playerListCommand")) {
            if (Server.getInstance().getOnlinePlayers().isEmpty()) {
                API.sendMessage(Main.config.getString("command_playerlist_empty"));
            } else {
                String playerListMessage = "";
                playerListMessage += "**" + Main.config.getString("command_playerlist_players") + " (" + Server.getInstance().getOnlinePlayers().size() + '/' + Server.getInstance().getMaxPlayers() + "):**";
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
        } else if (message.equalsIgnoreCase(Main.config.getString("commandPrefix") + "ip") && Main.config.getBoolean("ipCommand")) {
            API.sendMessage("```\n" + Main.config.getString("commands_ip_address") + ' ' + Main.config.getString("serverIp") + '\n' + Main.config.getString("commands_ip_port") + ' ' + Main.config.getString("serverPort") + "\n```");
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
        return Main.roleColors.get(hex) + role.getName();
    }
}
