package discordchat;

import cn.nukkit.Player;
import cn.nukkit.Server;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class DiscordListener extends ListenerAdapter {

    @SuppressWarnings("serial")
    Map<String, String> colors = new HashMap<String, String>()
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

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor() == null || e.getMember() == null || e.getAuthor().getId() == null || Main.jda == null || Main.jda.getSelfUser() == null || Main.jda.getSelfUser().getId() == null || e.getAuthor().equals(Main.jda.getSelfUser())) return;
        if (!e.getChannel().equals(Main.channel)) return;
        if (e.getAuthor().isBot()) return;
        String message = e.getMessage().getContentStripped();
        if (message.length() == 0 && e.getMessage().getAttachments().size() == 0) return;
        if (processPlayerListCommand(e, message)) return;
        String role = "";
        if (getRole(e.getMember()) != null) role = " \u00A7f| " + getRole(getRole(e.getMember()));
        if (!Main.config.getBoolean("enableDiscordToMinecraft")) return;
        String u00BB = " » ";
        if (Main.config.getBoolean("windowsHost")) u00BB = " \u00BB ";
        for (Player player : Server.getInstance().getOnlinePlayers().values()) player.sendMessage("\u00A7f[\u00A7bDiscord" + role + "\u00A7f] " + e.getMember().getEffectiveName() + u00BB + message);
    }

     private boolean processPlayerListCommand(GuildMessageReceivedEvent e, String message) {
        if (message.equalsIgnoreCase("!playerlist") && Main.config.getBoolean("playerListCommand")) {
            if (Server.getInstance().getOnlinePlayers().size() == 0) {
                Main.channel.sendMessage("**No online players**").queue();
            } else {
                String playerlistMessage = "";
                playerlistMessage += "**Online players (" + Server.getInstance().getOnlinePlayers().size() + "/" + Server.getInstance().getMaxPlayers() + "):**";
                playerlistMessage += "\n```\n";
                StringJoiner players = new StringJoiner(",");
                for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                    players.add(player.getName());
                }
                playerlistMessage += players.toString();
                if (playerlistMessage.length() > 1996) playerlistMessage = playerlistMessage.substring(0, 1993) + "...";
                playerlistMessage += "\n```";
                Main.channel.sendMessage(playerlistMessage).queue();
            }
        } else if (message.equalsIgnoreCase("!ip") && Main.config.getBoolean("ipCommand")) {
            Main.channel.sendMessage("```\nAddress: " + Main.config.getString("serverIp") + "\nPort: " + Main.config.getString("serverPort") + "\n```").queue();
        } else {
            return false;
        }
        return true;
    }

    private Role getRole(Member m) {
        for (Role role : m.getRoles()) return role;
        return null;
    }

    private String getRole(Role role) {
        if (role == null) return "";
        String hex = role.getColor() != null ? Integer.toHexString(role.getColor().getRGB()).toUpperCase() : "99AAB5";
        if (hex.length() == 8) hex = hex.substring(2);
        String color = colors.get(hex) + role.getName();
        return color;
    }
}