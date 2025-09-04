package me.petterim1.discordchat;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import java.util.regex.Pattern;

public class Loader extends PluginBase {

    static Loader instance;
    static Config config;
    static JDA jda;
    static String channelId;
    static String consoleChannelId;
    static boolean debug;
    static boolean queueMessages;
    static MessageQueue messageQueue;
    private static PlayerListener playerListener;
    static final DiscordCommandSender discordCommandSender = new DiscordCommandSender();
    static final DiscordListener discordListener = new DiscordListener();
    static final DiscordConsoleListener discordConsoleListener = new DiscordConsoleListener();
    static Pattern messageFilterRegex;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        config = getConfig();
        checkAndUpdateConfig();
        try {
            debug = config.getBoolean("debug");
            if (debug) {
                getLogger().notice("Running DiscordChat in debug mode");
            }
            String pattern = config.getString("messageFilterRegex");
            if (!pattern.isEmpty()) {
                if (debug) {
                    getLogger().info("DEBUG: Setting message filter to " + pattern);
                }
                messageFilterRegex = Pattern.compile(pattern);
            }
            if (debug) {
                getLogger().info("DEBUG: Logging in to Discord");
            }
            jda = JDABuilder.createDefault(config.getString("botToken")).build();
            if (debug) {
                getLogger().info("DEBUG: Waiting JDA");
            }
            jda.awaitReady();
            if (debug) {
                getLogger().info("DEBUG: Registering events for PlayerListener");
            }
            if (playerListener == null) {
                getServer().getPluginManager().registerEvents(playerListener = new PlayerListener(), this);
            }
            channelId = config.getString("channelId", "null");
            if (debug) {
                getLogger().info("DEBUG: Setting server channel id to " + channelId);
            }
            if (debug) {
                getLogger().info("DEBUG: Registering events for DiscordListener");
            }
            jda.addEventListener(discordListener);
            if (config.getBoolean("discordConsole")) {
                consoleChannelId = config.getString("consoleChannelId", "null");
                if (debug) {
                    getLogger().info("DEBUG: Setting console channel id to " + consoleChannelId);
                }
                if (debug) {
                    getLogger().info("DEBUG: Registering events for DiscordConsoleListener");
                }
                jda.addEventListener(discordConsoleListener);
                if (config.getBoolean("consoleStatusMessages")) {
                    API.sendToConsole(config.getString("console_status_server_start"));
                }
            }
            if (!config.getString("botStatus").isEmpty()) {
                if (debug) {
                    getLogger().info("DEBUG: Setting bot status to " + config.getString("botStatus"));
                }
                jda.getPresence().setActivity(Activity.of(Activity.ActivityType.DEFAULT, config.getString("botStatus")));
            }
            if (!config.getString("channelTopic").isEmpty()) {
                if (debug) {
                    getLogger().info("DEBUG: Setting channel topic to " + config.getString("channelTopic"));
                }
                API.setTopic(config.getString("channelTopic"));
            }
            //noinspection AssignmentUsedAsCondition
            if (queueMessages = config.getBoolean("queueMessages")) {
                if (debug) {
                    getLogger().info("DEBUG: Starting message queue");
                }
                getServer().getScheduler().scheduleDelayedRepeatingTask(this, messageQueue = new MessageQueue(), 20, 20, true);
            }
            if (jda.getGuilds().isEmpty()) {
                getLogger().notice("Your Discord bot is not on any server. See https://cloudburstmc.org/resources/discordchat.137/ if you need help with the setup.");
            }
            if (config.getBoolean("startMessages")) {
                API.sendMessage(config.getString("status_server_started"));
            }
            if (debug) {
                getLogger().info("DEBUG: Startup done successfully");
            }
        } catch (Exception e) {
            getLogger().error("There was an error while enabling DiscordChat", e);
        }
    }

    @Override
    public void onDisable() {
        if (config.getBoolean("stopMessages")) {
            API.sendMessage(config.getString("status_server_stopped"));
        }
        if (config.getBoolean("consoleStatusMessages") && config.getBoolean("discordConsole")) {
            API.sendToConsole(config.getString("console_status_server_stop"));
        }
        if (debug) {
            getLogger().info("DEBUG: Disabling the plugin");
        }
        if (jda != null) {
            if (messageQueue != null) {
                if (debug) {
                    getLogger().info("DEBUG: Sending previously queued messages");
                }
                messageQueue.run();
            }
            jda.shutdown();
            if (debug) {
                getLogger().info("DEBUG: JDA shutdown called");
            }
        }
    }

    private void checkAndUpdateConfig() {
        int current = 11;
        int ver = config.getInt("configVersion");
        if (ver != current) {
            if (debug) {
                getLogger().info("DEBUG: Attempting to update config version " + ver + " to " + current);
            }

            if (ver < 2) {
                saveResource("config.yml", true);
                config = getConfig();
                getLogger().warning("Outdated config file replaced. You will need to set your settings again.");
                return;
            }

            if (ver < 11) {
                config.remove("spamFilter");
                config.set("messageFilterRegex", "(?i)discord.*?\\..*?\\/|http.*?\\:.*?\\/\\/");
                config.set("messageFilterReplacement", "<link>");
            }

            if (ver < 10) {
                config.set("logConsoleCommands", true);
            }

            if (ver < 9) {
                config.set("command_generic_no_perm", "§cYou don't have permission to use this command");
            }

            if (ver < 8) {
                config.remove("roleColors");
                config.set("command_mute_success", "§aDiscord chat muted");
                config.set("command_mute_already_muted", "§cDiscord chat is already muted");
                config.set("command_unmute_success", "§aDiscord chat is no longer muted");
                config.set("command_unmute_not_muted", "§cDiscord chat is not muted");
            }

            if (ver < 7) {
                config.set("queueMessages", true);
            }

            if (ver < 6) {
                config.set("discordCommand", false);
                config.set("discordCommandOutput", "Join our Discord server at §e<put your invite here>§f!");
                config.set("discordToMinecraftChatFormatting", "§f[§bDiscord §f| %role%§f] %discordname% » %message%");
                config.set("minecraftToDiscordChatFormatting", "%username% » %message%");
            }

            if (ver < 5) {
                config.set("consoleStatusMessages", true);
                config.set("console_status_server_start", "The server is starting up...");
                config.set("console_status_server_stop", "The server is shutting down...");
            }

            if (ver < 4) {
                config.set("consoleRole", "");
                config.set("err_no_perm", "You don't have permission to run console commands");
            }

            if (ver < 3) {
                config.set("commandPrefix", "!");
            }

            config.set("configVersion", current);
            config.save();
            config = getConfig();
            getLogger().warning("Config file updated to version " + current);
        } else {
            if (debug) {
                getLogger().info("DEBUG: Config is up to date");
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return DiscordCommand.handleCommand(sender, command, label, args);
    }
}
