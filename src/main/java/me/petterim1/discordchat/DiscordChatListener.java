package me.petterim1.discordchat;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DiscordChatListener extends ListenerAdapter {

    static final List<String> chatMuted = new CopyOnWriteArrayList<>();
    static final List<DiscordChatReceiver> receivers = new ArrayList<>();

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent e) {
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
        String role = getColoredRole(getRole(e.getMember()));
        String out = Loader.config.getString("discordToMinecraftChatFormatting").replace("%role%", role).replace("%timestamp%", new Date(System.currentTimeMillis()).toString()).replace("%discordname%", name).replace("%message%", message);
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            if (!chatMuted.contains(player.getName())) {
                player.sendMessage(out);
            }
        }
        if (Loader.config.getBoolean("enableMessagesToConsole")) {
            Server.getInstance().getLogger().info(out);
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
        Color color = r.getColor();
        if (color == null) {
            return TextFormat.WHITE + r.getName();
        } else {
            return fromRGB(color.getRed(), color.getGreen(), color.getBlue()) + r.getName();
        }
    }

    // Source: https://minecraft.fandom.com/wiki/Formatting_codes
    private final static Map<TextFormat, ColorSet> COLORS = new HashMap<>();

    static {
        COLORS.put(TextFormat.BLACK, new ColorSet(0, 0, 0));
        COLORS.put(TextFormat.DARK_BLUE, new ColorSet(0, 0, 170));
        COLORS.put(TextFormat.DARK_GREEN, new ColorSet(0, 170, 0));
        COLORS.put(TextFormat.DARK_AQUA, new ColorSet(0, 170, 170));
        COLORS.put(TextFormat.DARK_RED, new ColorSet(170, 0, 0));
        COLORS.put(TextFormat.DARK_PURPLE, new ColorSet(170, 0, 170));
        COLORS.put(TextFormat.GOLD, new ColorSet(255, 170, 0));
        COLORS.put(TextFormat.GRAY, new ColorSet(170, 170, 170));
        COLORS.put(TextFormat.DARK_GRAY, new ColorSet(85, 85, 85));
        COLORS.put(TextFormat.BLUE, new ColorSet(85, 85, 255));
        COLORS.put(TextFormat.GREEN, new ColorSet(85, 255, 85));
        COLORS.put(TextFormat.AQUA, new ColorSet(85, 255, 255));
        COLORS.put(TextFormat.RED, new ColorSet(255, 85, 85));
        COLORS.put(TextFormat.LIGHT_PURPLE, new ColorSet(255, 85, 255));
        COLORS.put(TextFormat.YELLOW, new ColorSet(255, 255, 85));
        COLORS.put(TextFormat.WHITE, new ColorSet(255, 255, 255));
        COLORS.put(TextFormat.MINECOIN_GOLD, new ColorSet(221, 214, 5));
    }

    private static class ColorSet {

        final int red;
        final int green;
        final int blue;

        ColorSet(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }

    // Source: https://gist.github.com/mikroskeem/428f82fbf12f52f29cc6199482c77fb5
    private static TextFormat fromRGB(int r, int g, int b) {
        TreeMap<Integer, TextFormat> closest = new TreeMap<>();
        COLORS.forEach((color, set) -> {
            int red = Math.abs(r - set.red);
            int green = Math.abs(g - set.green);
            int blue = Math.abs(b - set.blue);
            closest.put(red + green + blue, color);
        });
        return closest.firstEntry().getValue();
    }
}
