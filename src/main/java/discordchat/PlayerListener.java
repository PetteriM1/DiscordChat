package discordchat;

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

    private String lastMessage;
    private String lastName;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (Main.config.getBoolean("joinMessages")) API.sendMessage(Main.config.getString("info_player_joined").replace("%player%", e.getPlayer().getName()));

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (Main.config.getBoolean("quitMessages")) API.sendMessage(Main.config.getString("info_player_left").replace("%player%", e.getPlayer().getName()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        if (Main.config.getBoolean("deathMessages")) API.sendMessage(Main.config.getString("info_player_death").replace("%death_message%", TextFormat.clean(textFromContainer(e.getDeathMessage()))));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(PlayerChatEvent e) {
        if (!e.isCancelled()) {
            if (!Main.config.getBoolean("enableMinecraftToDiscord")) return;
            String msg = e.getMessage();
            String name = e.getPlayer().getName();
            if (Main.config.getBoolean("spamFilter")) {
                if (msg.startsWith("Horion - the best minecraft bedrock utility mod - ")) return;
                if (msg.equals(lastMessage) && name.equals(lastName)) return;
                lastMessage = msg;
                lastName = name;
                msg = msg.replace("<@@!", "").replace("<<@@", "").replace("<!@", "").replace("<@&", "").replace("<@", "").replace("@here", "here").replace("@everyone", "everyone");
            }
            API.sendMessage(TextFormat.clean(name + " \u00BB " + msg));
        }
    }

    private static String textFromContainer(TextContainer container) {
        if (container instanceof TranslationContainer) {
            return Server.getInstance().getLanguage().translateString(container.getText(), ((TranslationContainer) container).getParameters());
        }
        return container.getText();
    }
}
