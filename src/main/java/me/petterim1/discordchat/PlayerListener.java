package me.petterim1.discordchat;

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        if (Loader.config.getBoolean("joinMessages")) {
            API.sendMessage(TextFormat.clean(Loader.config.getString("info_player_joined")
                    .replace("%player%", e.getPlayer().getName())
                    .replace("%join_message%", textFromContainer(e.getJoinMessage())), true));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        if (Loader.config.getBoolean("quitMessages") && e.getPlayer().spawned) {
            API.sendMessage(TextFormat.clean(Loader.config.getString("info_player_left")
                    .replace("%player%", e.getPlayer().getName())
                    .replace("%quit_message%", textFromContainer(e.getQuitMessage())), true));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        if (Loader.config.getBoolean("deathMessages")) {
            String message = TextFormat.clean(textFromContainer(e.getDeathMessage()), true)
                    .replace("@", "[at]");
            if (Loader.messageFilterRegex != null) {
                message = Loader.messageFilterRegex.matcher(message)
                        .replaceAll(Loader.config.getString("messageFilterReplacement"));
            }
            if (message.trim().isEmpty()) {
                return;
            }
            API.sendMessage(Loader.config.getString("info_player_death")
                    .replace("%player%", e.getEntity().getName())
                    .replace("%death_message%", message));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(PlayerChatEvent e) {
        if (!Loader.config.getBoolean("enableMinecraftToDiscord")) {
            return;
        }
        String message = e.getMessage()
                .replace("@", "[at]");
        if (Loader.messageFilterRegex != null) {
            message = Loader.messageFilterRegex.matcher(message)
                    .replaceAll(Loader.config.getString("messageFilterReplacement"));
        }
        if (message.trim().isEmpty()) {
            return;
        }
        API.sendMessage(TextFormat.clean(Loader.config.getString("minecraftToDiscordChatFormatting")
                .replace("%username%", e.getPlayer().getName())
                .replace("%displayname%", e.getPlayer().getDisplayName())
                .replace("%message%", message), true));
    }

    private static String textFromContainer(TextContainer container) {
        if (container instanceof TranslationContainer) {
            return Server.getInstance().getLanguage()
                    .translateString(container.getText(), ((TranslationContainer) container).getParameters());
        }
        return container.getText();
    }
}
