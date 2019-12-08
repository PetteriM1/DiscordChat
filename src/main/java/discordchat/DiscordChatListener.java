package discordchat;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class DiscordChatListener extends ListenerAdapter {

    @SuppressWarnings("serial")
    private static final Map<String, String> colors = new HashMap<String, String>()
    {
        {
            put("99AAB5", "\u00A7f");
            put("1ABC9C", "\u00A7a");
            put("2ECC71", "\u00A7a");
            put("3498DB", "\u00A73");
            put("9B59B6", "\u00A75");
            put("E91E63", "\u00A7d");
            put("F1C40F", "\u00A7e");
            put("E67E22", "\u00A76");
            put("E74C3C", "\u00A7c");
            put("95A5A6", "\u00A77");
            put("607D8B", "\u00A78");
            put("11806A", "\u00A72");
            put("1F8B4C", "\u00A72");
            put("206694", "\u00A71");
            put("71368A", "\u00A75");
            put("AD1457", "\u00A7d");
            put("C27C0E", "\u00A76");
            put("A84300", "\u00A76");
            put("992D22", "\u00A74");
            put("979C9F", "\u00A77");
            put("546E7A", "\u00A78");
        }
    };

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
        String name = e.getMember().getEffectiveName();
        if (Main.config.getBoolean("spamFilter")) {
            if (message.equals(lastMessage) && name.equals(lastName)) return;
            lastMessage = message;
            lastName = name;
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
                String playerlistMessage = "";
                playerlistMessage += "**" + Main.config.getString("command_playerlist_players") + " (" + Server.getInstance().getOnlinePlayers().size() + '/' + Server.getInstance().getMaxPlayers() + "):**";
                playerlistMessage += "\n```\n";
                StringJoiner players = new StringJoiner(", ");
                for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                    players.add(player.getName());
                }
                playerlistMessage += players.toString();
                if (playerlistMessage.length() > 1996) playerlistMessage = playerlistMessage.substring(0, 1993) + "...";
                playerlistMessage += "\n```";
                API.sendMessage(playerlistMessage);
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
        return colors.get(hex) + role.getName();
    }
}
