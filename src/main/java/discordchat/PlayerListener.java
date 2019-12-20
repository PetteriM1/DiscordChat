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

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (Main.config.getBoolean("joinMessages")) Main.sendMessage("**:heavy_plus_sign: " + e.getPlayer().getName() + " joined the server**");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (Main.config.getBoolean("quitMessages")) Main.sendMessage("**:heavy_minus_sign: " + e.getPlayer().getName() + " left the server**");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        String msg = TextFormat.clean(textFromContainer(e.getDeathMessage()));
        if (Main.config.getBoolean("deathMessages")) Main.sendMessage("**:skull: `" + msg + "`**");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(PlayerChatEvent e) {
        if (!Main.config.getBoolean("enableMinecraftToDiscord")) return;
        if (!e.isCancelled()) Main.sendMessage(TextFormat.clean(e.getPlayer().getName() + " \u00BB `" + e.getMessage() + "`"));
    }

    private String textFromContainer(TextContainer container) {
        if (container instanceof TranslationContainer) {
            return Server.getInstance().getLanguage().translateString(container.getText(), ((TranslationContainer) container).getParameters());
        }
        return container.getText();
    }
}
