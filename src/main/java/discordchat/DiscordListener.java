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

    Map<String, String> colors = new HashMap<String, String>()
    {
        {
            //DEFAULT MINECRAFT COLORS
            put("000000", "\u00A70"); //black
            put("0000AA", "\u00A71"); //dark blue
            put("00AA00", "\u00A72"); //dark green 
            put("00AAAA", "\u00A73"); //dark aqua
            put("AA0000", "\u00A74"); //dark red
            put("AA00AA", "\u00A75"); //dark purple if you think its kinda to light like i do then change it to 2A002A
            put("FFAA00", "\u00A76"); //gold
            put("AAAAAA", "\u00A77"); //gray
            put("555555", "\u00A78"); //dark gray
            put("5555FF", "\u00A79"); //blue 
            put("55FF55", "\u00A7a"); //green
            put("55FFFF", "\u00A7b"); //aqua
            put("FF5555", "\u00A7c"); //red
            put("FF55FF", "\u00A7d"); //light purple
            put("FFFF55", "\u00A7e"); //yellow
            put("FFFFFF", "\u00A7f"); //white
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
        if (message.contains("ঋ") || message.contains("ༀ") || message.contains("")) return;
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
                StringJoiner players = new StringJoiner(", ");
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
