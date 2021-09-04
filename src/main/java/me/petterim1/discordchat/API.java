package me.petterim1.discordchat;

import cn.nukkit.Server;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class API {

    /**
     * Send a message to the default channel on Discord.
     * @param message Message
     */
    public static void sendMessage(String message) {
        sendMessage(Loader.channelId, message);
    }

    /**
     * Send message to a given channel on Discord.
     * @param channelId Channel ID
     * @param message Message
     */
    public static void sendMessage(String channelId, String message) {
        if (Loader.jda != null) {
            TextChannel channel = Loader.jda.getTextChannelById(channelId);
            if (channel != null) {
                channel.sendMessage(message).queue();
            } else if (Loader.debug) {
                Server.getInstance().getLogger().error("TextChannel is null: " + channelId);
            }
        } else if (Loader.debug) {
            Server.getInstance().getLogger().error("JDA is null");
        }
    }

    /**
     * Send a message to the Discord console channel if the channel exists.
     * @param message Message
     */
    public static void sendToConsole(String message) {
        if (Loader.jda != null) {
            TextChannel channel = Loader.jda.getTextChannelById(Loader.consoleChannelId);
            if (channel != null) {
                channel.sendMessage(message).queue();
            } else if (Loader.debug) {
                Server.getInstance().getLogger().error("TextChannel for console is null: " + Loader.consoleChannelId);
            }
        } else if (Loader.debug) {
            Server.getInstance().getLogger().error("JDA is null");
        }
    }

    /**
     * Set the channel topic of the default channel on Discord. Rate limits apply.
     * @param topic New channel topic
     */
    public static void setTopic(String topic) {
        setTopic(Loader.channelId, topic);
    }

    /**
     * Set the channel topic of a given channel on Discord. Rate limits apply.
     * @param channelId Channel ID
     * @param topic New channel topic
     */
    public static void setTopic(String channelId, String topic) {
        if (Loader.jda != null) {
            TextChannel channel = Loader.jda.getTextChannelById(channelId);
            if (channel != null) {
                channel.getManager().setTopic(topic).queue();
            } else if (Loader.debug) {
                Server.getInstance().getLogger().error("TextChannel is null: " + channelId);
            }
        } else if (Loader.debug) {
            Server.getInstance().getLogger().error("JDA is null");
        }
    }

    /**
     * Register your DiscordChatReceiver.
     * @param receiver Your class that implements DiscordChatReceiver
     */
    @SuppressWarnings("unused")
    public static void registerReceiver(DiscordChatReceiver receiver) {
        DiscordChatListener.receivers.add(receiver);
    }

    /**
     * Get all registered DiscordChatReceivers.
     * @return List of all registered DiscordChatReceivers
     */
    @SuppressWarnings("unused")
    public static List<DiscordChatReceiver> getReceiversList() {
        return DiscordChatListener.receivers;
    }
}
