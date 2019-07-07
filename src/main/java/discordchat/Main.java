package discordchat;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.TextChannel;

public class Main extends PluginBase {

    static Config config;
    static JDA jda;
    static String channelId;
    private static boolean debug;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        debug = config.getBoolean("debug");
        if (debug) getServer().getLogger().info("Registering events for PlayerListener");
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        try {
            if (debug) getServer().getLogger().info("Logging in with bot token " + config.getString("botToken", "null"));
            jda = new JDABuilder(AccountType.BOT).setToken(config.getString("botToken")).buildBlocking();
            if (debug) getServer().getLogger().info("Set server channel id to " + config.getString("channelId", "null"));
            channelId = config.getString("channelId");
            if (debug) getServer().getLogger().info("Registering events for DiscordListener");
            jda.addEventListener(new DiscordListener());
            if (config.getBoolean("discordConsole")) {
                jda.addEventListener(new DiscordConsole());
                if (debug) getServer().getLogger().info("Registering events for DiscordConsole");
            }
            if (debug) getServer().getLogger().info("Set bot status to " + config.getString("botStatus"));
            jda.getPresence().setGame(Game.of(Game.GameType.DEFAULT, config.getString("botStatus")));
            if (debug) getServer().getLogger().info("Set channel topic to " + config.getString("channelTopic"));
            if (!config.getString("channelTopic").isEmpty()) jda.getTextChannelById(channelId).getManager().setTopic(config.getString("channelTopic"));
            if (debug && jda.getGuilds().isEmpty()) getServer().getLogger().warning("Your Discord bot is not on any server");
            if (debug) getServer().getLogger().info("Startup done successfully");
        } catch (Exception e) {
            getLogger().error("Couldn't enable Discord chat sync");
            if (debug) e.printStackTrace();
        }
        if (config.getBoolean("startMessages")) sendMessage(config.getString("status_server_started"));
    }

    @Override
    public void onDisable() {
        if (config.getBoolean("stopMessages")) sendMessage(config.getString("status_server_stopped"));
        if (debug) getServer().getLogger().info("Disabling the plugin");
    }

    public static void sendMessage(String message) {
        if (jda != null) {
            TextChannel channel = jda.getTextChannelById(channelId);
            if (channel != null) {
                channel.sendMessage(message).queue();
            } else if (debug) {
                Server.getInstance().getLogger().info("TextChannel is null");
            }
        } else if (debug) {
            Server.getInstance().getLogger().info("JDA is null");
        }
    }
}