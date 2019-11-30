package discordchat;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;

public class Main extends PluginBase {

    static Config config;
    static JDA jda;
    static String channelId;
    static boolean debug;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        checkAndUpdateConfig();
        debug = config.getBoolean("debug");
        if (debug) getServer().getLogger().info("Registering events for PlayerListener");
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        try {
            if (debug) getServer().getLogger().info("Logging in with bot token " + config.getString("botToken", "null"));
            jda = new JDABuilder(config.getString("botToken")).build();
            if (debug) getServer().getLogger().info("Waiting JDA...");
            jda.awaitReady();
            if (debug) getServer().getLogger().info("Setting server channel id to " + config.getString("channelId", "null"));
            channelId = config.getString("channelId");
            if (debug) getServer().getLogger().info("Registering events for DiscordListener");
            jda.addEventListener(new DiscordListener());
            if (config.getBoolean("discordConsole")) {
                if (debug) getServer().getLogger().info("Registering events for DiscordConsole");
                jda.addEventListener(new DiscordConsole());
            }
            if (debug) getServer().getLogger().info("Setting bot status to " + config.getString("botStatus"));
            jda.getPresence().setActivity(Activity.of(Activity.ActivityType.DEFAULT, config.getString("botStatus")));
            if (!config.getString("channelTopic").isEmpty()) {
                if (debug) getServer().getLogger().info("Setting channel topic to " + config.getString("channelTopic"));
                TextChannel ch = jda.getTextChannelById(channelId);
                if (ch != null) {
                    ch.getManager().setTopic(config.getString("channelTopic")).queue();
                } else if (debug) {
                    getLogger().error("TextChannel is null");
                }
            }
            if (debug && jda.getGuilds().isEmpty()) getServer().getLogger().warning("Your Discord bot is not on any server");
            if (debug) getServer().getLogger().info("Startup done successfully");
        } catch (Exception e) {
            getLogger().error("Couldn't enable Discord chat sync");
            if (debug) e.printStackTrace();
        }
        if (config.getBoolean("startMessages")) API.sendMessage(config.getString("status_server_started"));
    }

    @Override
    public void onDisable() {
        if (config.getBoolean("stopMessages")) API.sendMessage(config.getString("status_server_stopped"));
        if (debug) getServer().getLogger().info("Disabling the plugin");
    }

    private void checkAndUpdateConfig() {
        if (config.getInt("configVersion") != 2) {
            saveResource("config.yml", true);
            config = getConfig();
            getLogger().warning("Outdated config file replaced. You will need to set your settings again.");
        }
    }
}
