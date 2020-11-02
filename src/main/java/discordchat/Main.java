package discordchat;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class Main extends PluginBase {

    static Main instance;
    static Config config;
    static JDA jda;
    static String channelId;
    static String consoleChannelId;
    static boolean debug;
    static DiscordCommandSender discordCommandSender;
    static Map<String, String> roleColors;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        config = getConfig();
        checkAndUpdateConfig();
        debug = config.getBoolean("debug");
        if (debug) getServer().getLogger().info("Loading role color map from config");
        roleColors = (Map<String, String>) config.get("roleColors");
        if (debug) getServer().getLogger().info("Registering events for PlayerListener");
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        try {
            if (debug) getServer().getLogger().info("Logging in...");
            jda = JDABuilder.createDefault(config.getString("botToken")).build();
            if (debug) getServer().getLogger().info("Waiting JDA...");
            jda.awaitReady();
            if (debug) getServer().getLogger().info("Setting server channel id to " + config.getString("channelId", "null"));
            channelId = config.getString("channelId");
            if (debug) getServer().getLogger().info("Registering events for DiscordListener");
            jda.addEventListener(new DiscordChatListener());
            if (config.getBoolean("discordConsole")) {
                if (debug) getServer().getLogger().info("Creating new DiscordCommandSender");
                discordCommandSender = new DiscordCommandSender();
                if (debug) getServer().getLogger().info("Setting console channel id to " + config.getString("consoleChannelId", "null"));
                consoleChannelId = config.getString("consoleChannelId");
                if (debug) getServer().getLogger().info("Registering events for DiscordConsole");
                jda.addEventListener(new DiscordConsoleListener());
                if (config.getBoolean("consoleStatusMessages")) API.sendToConsole(config.getString("console_status_server_start"));
            }
            if (!config.getString("botStatus").isEmpty()) {
                if (debug) getServer().getLogger().info("Setting bot status to " + config.getString("botStatus"));
                jda.getPresence().setActivity(Activity.of(Activity.ActivityType.DEFAULT, config.getString("botStatus")));
            }
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
        if (config.getBoolean("consoleStatusMessages")) API.sendToConsole(config.getString("console_status_server_stop"));
        if (debug) getServer().getLogger().info("Disabling the plugin");
    }

    private void checkAndUpdateConfig() {
        if (config.getInt("configVersion") != 5) {
            int updated = 0;
            if (config.getInt("configVersion") == 2) {
                config.set("commandPrefix", "!");
                config.set("consoleRole", "");
                config.set("err_no_perm", "Your role doesn't have permission to run console commands");
                config.set("consoleStatusMessages", true);
                config.set("console_status_server_start", "The server is starting up...");
                config.set("console_status_server_stop", "The server is shutting down...");
                config.set("roleColors", new HashMap<String, String>() {
                    {
                        put("99AAB5", "§f");
                        put("1ABC9C", "§a");
                        put("2ECC71", "§a");
                        put("3498DB", "§3");
                        put("9B59B6", "§5");
                        put("E91E63", "§d");
                        put("F1C40F", "§e");
                        put("E67E22", "§6");
                        put("E74C3C", "§c");
                        put("95A5A6", "§7");
                        put("607D8B", "§8");
                        put("11806A", "§2");
                        put("1F8B4C", "§2");
                        put("206694", "§1");
                        put("71368A", "§5");
                        put("AD1457", "§d");
                        put("C27C0E", "§6");
                        put("A84300", "§6");
                        put("992D22", "§4");
                        put("979C9F", "§7");
                        put("546E7A", "§8");
                    }
                });
                updated = 2;
            } else if (config.getInt("configVersion") == 3) {
                config.set("consoleRole", "");
                config.set("err_no_perm", "Your role doesn't have permission to run console commands");
                config.set("consoleStatusMessages", true);
                config.set("console_status_server_start", "The server is starting up...");
                config.set("console_status_server_stop", "The server is shutting down...");
                config.set("roleColors", new HashMap<String, String>() {
                    {
                        put("99AAB5", "§f");
                        put("1ABC9C", "§a");
                        put("2ECC71", "§a");
                        put("3498DB", "§3");
                        put("9B59B6", "§5");
                        put("E91E63", "§d");
                        put("F1C40F", "§e");
                        put("E67E22", "§6");
                        put("E74C3C", "§c");
                        put("95A5A6", "§7");
                        put("607D8B", "§8");
                        put("11806A", "§2");
                        put("1F8B4C", "§2");
                        put("206694", "§1");
                        put("71368A", "§5");
                        put("AD1457", "§d");
                        put("C27C0E", "§6");
                        put("A84300", "§6");
                        put("992D22", "§4");
                        put("979C9F", "§7");
                        put("546E7A", "§8");
                    }
                });
                updated = 3;
            } else if (config.getInt("configVersion") == 4) {
                config.set("consoleStatusMessages", true);
                config.set("console_status_server_start", "The server is starting up...");
                config.set("console_status_server_stop", "The server is shutting down...");
                config.set("roleColors", new HashMap<String, String>() {
                    {
                        put("99AAB5", "§f");
                        put("1ABC9C", "§a");
                        put("2ECC71", "§a");
                        put("3498DB", "§3");
                        put("9B59B6", "§5");
                        put("E91E63", "§d");
                        put("F1C40F", "§e");
                        put("E67E22", "§6");
                        put("E74C3C", "§c");
                        put("95A5A6", "§7");
                        put("607D8B", "§8");
                        put("11806A", "§2");
                        put("1F8B4C", "§2");
                        put("206694", "§1");
                        put("71368A", "§5");
                        put("AD1457", "§d");
                        put("C27C0E", "§6");
                        put("A84300", "§6");
                        put("992D22", "§4");
                        put("979C9F", "§7");
                        put("546E7A", "§8");
                    }
                });
                updated = 4;
            } else {
                saveResource("config.yml", true);
                config = getConfig();
                getLogger().warning("Outdated config file replaced. You will need to set your settings again.");
            }
            if (updated > 1) {
                config.set("configVersion", 5);
                config.save();
                config = getConfig();
                getLogger().warning("Config file updated [" + updated + " -> 5]");
            }
        }
    }
}
