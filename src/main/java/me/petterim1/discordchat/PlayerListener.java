package me.petterim1.discordchat;

import org.cloudburstmc.server.event.EventPriority;
import org.cloudburstmc.server.event.Listener;
import org.cloudburstmc.server.event.player.PlayerChatEvent;
import org.cloudburstmc.server.event.player.PlayerDeathEvent;
import org.cloudburstmc.server.event.player.PlayerJoinEvent;
import org.cloudburstmc.server.event.player.PlayerQuitEvent;
import org.cloudburstmc.server.utils.TextFormat;

import java.util.Date;

public class PlayerListener {

    private String lastMessage;
    private String lastName;

    @Listener
    public void onJoin(PlayerJoinEvent e) {
        if (Loader.config.getBoolean("joinMessages")) API.sendMessage(Loader.config.getString("info_player_joined").replace("%player%", e.getPlayer().getName()).replace("%join_message%", TextFormat.clean(API.textFromContainer(e.getJoinMessage()))));
    }

    @Listener
    public void onQuit(PlayerQuitEvent e) {
        if (Loader.config.getBoolean("quitMessages") && e.getPlayer().spawned) {
            API.sendMessage(Loader.config.getString("info_player_left").replace("%player%", e.getPlayer().getName()).replace("%quit_message%", TextFormat.clean(API.textFromContainer(e.getQuitMessage()))));
        }
    }

    @Listener(ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        if (Loader.config.getBoolean("deathMessages")) API.sendMessage(Loader.config.getString("info_player_death").replace("%death_message%", TextFormat.clean(API.textFromContainer(e.getDeathMessage()))));
    }

    @Listener(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(PlayerChatEvent e) {
        if (!Loader.config.getBoolean("enableMinecraftToDiscord")) return;
        String message = e.getMessage();
        String name = e.getPlayer().getName();
        if (Loader.config.getBoolean("spamFilter")) {
            if (message.startsWith("Horion - the best minecraft bedrock utility mod - ")) return;
            if (message.equals(lastMessage) && name.equals(lastName)) return;
            lastMessage = message;
            lastName = name;
            message = message.replace("@", "[at]");
        }
        API.sendMessage(TextFormat.clean(Loader.config.getString("minecraftToDiscordChatFormatting")).replace("%timestamp%", new Date(System.currentTimeMillis()).toString()).replace("%username%", name).replace("%displayname%", e.getPlayer().getDisplayName()).replace("%message%", message));
    }
}
