package discordchat;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

public class Main extends PluginBase {

    public static JDA jda;
    public static Guild server;
    public static TextChannel channel;
    public static Config config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        config = getConfig();
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(config.getString("botToken")).buildBlocking();
            server = jda.getGuildById(config.getString("serverId"));
            channel = jda.getTextChannelById(config.getString("channelId"));
            DiscordListener dcl = new DiscordListener();
            jda.addEventListener(dcl);
            dcl.colors.put("99AAB5", "\u00A7f");
            dcl.colors.put("1ABC9C", "\u00A7a");
            dcl.colors.put("2ECC71", "\u00A7a");
            dcl.colors.put("3498DB", "\u00A73");
            dcl.colors.put("9B59B6", "\u00A75");
            dcl.colors.put("E91E63", "\u00A7d");
            dcl.colors.put("F1C40F", "\u00A7e");
            dcl.colors.put("E67E22", "\u00A76");
            dcl.colors.put("E74C3C", "\u00A7c");
            dcl.colors.put("95A5A6", "\u00A77");
            dcl.colors.put("607D8B", "\u00A78");
            dcl.colors.put("11806A", "\u00A72");
            dcl.colors.put("1F8B4C", "\u00A72");
            dcl.colors.put("206694", "\u00A71");
            dcl.colors.put("71368A", "\u00A75");
            dcl.colors.put("AD1457", "\u00A7d");
            dcl.colors.put("C27C0E", "\u00A76");
            dcl.colors.put("A84300", "\u00A76");
            dcl.colors.put("992D22", "\u00A74");
            dcl.colors.put("979C9F", "\u00A77");
            dcl.colors.put("546E7A", "\u00A78");
            jda.getPresence().setGame(Game.of(Game.GameType.DEFAULT, config.getString("botStatus")));
        } catch (Exception e) {
            getLogger().error("Couldn't enable discord chat sync");
            if (config.getBoolean("debug")) e.printStackTrace();
        }
    }
}