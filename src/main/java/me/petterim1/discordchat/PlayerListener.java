package me.petterim1.discordchat;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.utils.TextFormat;

import java.util.Date;

public class PlayerListener implements Listener {

    private String lastMessage;
    private String lastName;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (Loader.config.getBoolean("joinMessages")) API.sendMessage(Loader.config.getString("info_player_joined").replace("%player%", e.getPlayer().getName()).replace("%join_message%", TextFormat.clean(API.textFromContainer(e.getJoinMessage()))));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (Loader.config.getBoolean("quitMessages") && e.getPlayer().spawned) {
            API.sendMessage(Loader.config.getString("info_player_left").replace("%player%", e.getPlayer().getName()).replace("%quit_message%", TextFormat.clean(API.textFromContainer(e.getQuitMessage()))));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        if (Loader.config.getBoolean("deathMessages")) API.sendMessage(Loader.config.getString("info_player_death").replace("%death_message%", TextFormat.clean(API.textFromContainer(e.getDeathMessage()))));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
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
