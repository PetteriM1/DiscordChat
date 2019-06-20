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

    private boolean debug;

    public static JDA jda;
    public static Guild server;
    public static TextChannel channel;
    public static Config config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        debug = config.getBoolean("debug");
        if (debug) getServer().getLogger().info("Registering events for PlayerListener");
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        try {
            if (debug) getServer().getLogger().info("Logging in with bot token " + config.getString("botToken", "\u00A7cnull"));
            jda = new JDABuilder(AccountType.BOT).setToken(config.getString("botToken")).buildBlocking();
            if (debug) getServer().getLogger().info("Set server id to " + config.getString("serverId", "\u00A7cnull"));
            server = jda.getGuildById(config.getString("serverId"));
            if (debug) getServer().getLogger().info("Set server channel id to " + config.getString("channelId", "\u00A7cnull"));
            channel = jda.getTextChannelById(config.getString("channelId"));
            if (debug) getServer().getLogger().info("Registering events for DiscordListener");
            jda.addEventListener(new DiscordListener());
            if (debug) getServer().getLogger().info("Set bot status to " + config.getString("botStatus"));
            jda.getPresence().setGame(Game.of(Game.GameType.DEFAULT, config.getString("botStatus")));
            if (debug && jda.getGuilds().isEmpty()) getServer().getLogger().warning("Your Discord bot is not in any guild");
            if (debug) getServer().getLogger().info("Startup done successfully");
        } catch (Exception e) {
            getLogger().error("Couldn't enable Discord chat sync");
            if (debug) e.printStackTrace();
        }
        if (jda != null && config.getBoolean("startMessages")) channel.sendMessage("**:white_check_mark: Server started!**").queue();
    }

    @Override
    public void onDisable() {
        if (jda != null && config.getBoolean("stopMessages")) channel.sendMessage("**:x: Server stopped!**").queue();
        if (debug) getServer().getLogger().info("Disabling the plugin");
    }
}