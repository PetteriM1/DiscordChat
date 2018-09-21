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

    public Map<String, String> colors = new HashMap<>();

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
        for (Player player : Server.getInstance().getOnlinePlayers().values()) player.sendMessage("\u00A7f[\u00A7bDiscord" + role + "\u00A7f] " + e.getMember().getEffectiveName() + " » " + message);
    }

     private boolean processPlayerListCommand(GuildMessageReceivedEvent e, String message) {
        if (!Main.config.getBoolean("playerListCommand")) return false;
        if (message.equalsIgnoreCase("!playerlist")) {
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
                return true;
            }
        }
        return false;
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