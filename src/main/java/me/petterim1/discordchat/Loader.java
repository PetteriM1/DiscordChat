package me.petterim1.discordchat;

import com.google.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.event.Listener;
import org.cloudburstmc.server.event.server.ServerShutdownEvent;
import org.cloudburstmc.server.event.server.ServerStartEvent;
import org.cloudburstmc.server.plugin.Plugin;
import org.cloudburstmc.server.plugin.PluginDescription;
import org.cloudburstmc.server.utils.Config;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;

@Plugin(id = "me.petterim1.discordchat",
        name = "DiscordChat",
        version = "2.0.cloudburst.bleeding.1",
        description = "Sync Discord chat with your Cloudburst server",
        url = "https://cloudburstmc.org/resources/discordchat.137/",
        authors = "PetteriM1")
public class Loader {

    static Logger logger;
    static Loader instance;
    static Config config;
    static JDA jda;
    static String channelId;
    static String consoleChannelId;
    static boolean debug;
    static DiscordCommandSender discordCommandSender;
    static Map<String, String> roleColors;

    @Inject
    private Loader(Logger logger, PluginDescription description, Path dataFolder, Server server) {
        Loader.logger = logger;
        try {
            dataFolder.toFile().mkdirs();
            File file = new File(dataFolder + "/config.yml");
            if (!file.isFile()) {
                InputStream stream = null;
                OutputStream resStreamOut = null;
                try {
                    stream = Loader.class.getResourceAsStream("/config.yml");
                    int readBytes;
                    byte[] buffer = new byte[1024];
                    resStreamOut = new FileOutputStream(dataFolder + "/config.yml");
                    while ((readBytes = stream.read(buffer)) > 0) {
                        resStreamOut.write(buffer, 0, readBytes);
                    }
                } catch (Exception ex) {
                    logger.error("Failed to save config.yml", ex);
                } finally {
                    try {
                        stream.close();
                        resStreamOut.close();
                    } catch (Exception ex) {
                        logger.error("Error", ex);
                    }
                }
            }
            config = new Config(dataFolder + "/config.yml", Config.YAML);
        } catch (Exception ex) {
            logger.error("Failed to load config.yml", ex);
        }
    }

    @Listener
    public void onStart(ServerStartEvent event) {
        instance = this;
        debug = config.getBoolean("debug");
        if (debug) logger.info("Loading role color map from config");
        roleColors = (Map<String, String>) config.get("roleColors");
        if (debug) logger.info("Registering events for PlayerListener");
        Server.getInstance().getEventManager().registerListeners(this, new PlayerListener());
        try {
            if (debug) logger.info("Logging in to Discord...");
            jda = JDABuilder.createDefault(config.getString("botToken")).build();
            if (debug) logger.info("Waiting JDA...");
            jda.awaitReady();
            if (debug) logger.info("Setting server channel id to " + config.getString("channelId", "null"));
            channelId = config.getString("channelId");
            if (debug) logger.info("Registering events for DiscordListener");
            jda.addEventListener(new DiscordChatListener());
            if (config.getBoolean("discordConsole")) {
                if (debug) logger.info("Creating new DiscordCommandSender");
                discordCommandSender = new DiscordCommandSender();
                if (debug) logger.info("Setting console channel id to " + config.getString("consoleChannelId", "null"));
                consoleChannelId = config.getString("consoleChannelId");
                if (debug) logger.info("Registering events for DiscordConsole");
                jda.addEventListener(new DiscordConsoleListener());
                if (config.getBoolean("consoleStatusMessages")) API.sendToConsole(config.getString("console_status_server_start"));
            }
            if (!config.getString("botStatus").isEmpty()) {
                if (debug) logger.info("Setting bot status to " + config.getString("botStatus"));
                jda.getPresence().setActivity(Activity.of(Activity.ActivityType.DEFAULT, config.getString("botStatus")));
            }
            if (!config.getString("channelTopic").isEmpty()) {
                if (debug) logger.info("Setting channel topic to " + config.getString("channelTopic"));
                TextChannel ch = jda.getTextChannelById(channelId);
                if (ch != null) {
                    ch.getManager().setTopic(config.getString("channelTopic")).queue();
                } else if (debug) {
                    logger.error("TextChannel is null");
                }
            }
            if (debug && jda.getGuilds().isEmpty()) logger.warn("Your Discord bot is not on any server");
            if (debug) logger.info("Startup done successfully");
        } catch (Exception e) {
            logger.error("Couldn't enable Discord chat sync");
            if (debug) e.printStackTrace();
        }
        if (config.getBoolean("startMessages")) API.sendMessage(config.getString("status_server_started"));
    }

    @Listener
    public void onShutdown(ServerShutdownEvent event) {
        if (config.getBoolean("stopMessages")) API.sendMessage(config.getString("status_server_stopped"));
        if (config.getBoolean("consoleStatusMessages")) API.sendToConsole(config.getString("console_status_server_stop"));
        if (debug) logger.info("Disabling the plugin");
    }

    // TODO: Rewrite something similar
    /*private void checkAndUpdateConfig() {
        if (config.getInt("configVersion") != 6) {
            int updated = 0;
            if (config.getInt("configVersion") == 2) {
                config.set("commandPrefix", "!");
                config.set("consoleRole", "");
                config.set("err_no_perm", "You don't have permission to run console commands");
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
                config.set("discordCommand", false);
                config.set("discordCommandOutput", "Join our Discord server at §e<put your invite here>§f!");
                config.set("discordToMinecraftChatFormatting", "§f[§bDiscord §f| %role%§f] %discordname% » %message%");
                config.set("minecraftToDiscordChatFormatting", "%username% » %message%");
                updated = 2;
            } else if (config.getInt("configVersion") == 3) {
                config.set("consoleRole", "");
                config.set("err_no_perm", "You don't have permission to run console commands");
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
                config.set("discordCommand", false);
                config.set("discordCommandOutput", "Join our Discord server at §e<put your invite here>§f!");
                config.set("discordToMinecraftChatFormatting", "§f[§bDiscord §f| %role%§f] %discordname% » %message%");
                config.set("minecraftToDiscordChatFormatting", "%username% » %message%");
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
                config.set("discordCommand", false);
                config.set("discordCommandOutput", "Join our Discord server at §e<put your invite here>§f!");
                config.set("discordToMinecraftChatFormatting", "§f[§bDiscord §f| %role%§f] %discordname% » %message%");
                config.set("minecraftToDiscordChatFormatting", "%username% » %message%");
                updated = 4;
            } else if (config.getInt("configVersion") == 5) {
                config.set("discordCommand", false);
                config.set("discordCommandOutput", "Join our Discord server at §e<put your invite here>§f!");
                config.set("discordToMinecraftChatFormatting", "§f[§bDiscord §f| %role%§f] %discordname% » %message%");
                config.set("minecraftToDiscordChatFormatting", "%username% » %message%");
                updated = 5;
            } else {
                saveResource("config.yml", true);
                config = getConfig();
                logger.warn("Outdated config file replaced. You will need to set your settings again.");
            }
            if (updated > 1) {
                config.set("configVersion", 6);
                config.save();
                config.reload();
                logger.warn("Config file updated [" + updated + " -> 6]");
            }
        }
    }*/

    // TODO: Port this code for Cloudburst
    /*@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (config.getBoolean("discordCommand") && command.getName().equalsIgnoreCase("discord")) {
            sender.sendMessage(config.getString("discordCommandOutput"));
            return true;
        }
        return false;
    }*/
}
