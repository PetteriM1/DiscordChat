package me.petterim1.discordchat;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class API {

    /**
     * Send a message to the default channel on Discord.
     *
     * @param message Message
     */
    public static void sendMessage(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        if (Loader.queueMessages) {
            MessageQueue.defaultChat.add(message);
        } else {
            sendMessageInternal(Loader.channelId, message);
        }
    }

    /**
     * Send message to a given channel on Discord.
     *
     * @param channelId Channel ID
     * @param message   Message
     */
    @SuppressWarnings("unused")
    public static void sendMessage(String channelId, String message) {
        if (channelId == null) {
            throw new IllegalArgumentException("Channel id cannot be null");
        }
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        if (Loader.queueMessages) {
            Queue<String> channel = MessageQueue.customChat.get(channelId);
            if (channel == null) {
                channel = new ConcurrentLinkedQueue<>();
                channel.add(message);
                MessageQueue.customChat.put(channelId, channel);
            } else {
                channel.add(message);
            }
        } else {
            sendMessageInternal(channelId, message);
        }
    }

    /**
     * Send a message to the Discord console channel if the channel exists.
     *
     * @param message Message
     */
    public static void sendToConsole(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        if (Loader.queueMessages) {
            MessageQueue.console.add(message);
        } else {
            sendMessageInternal(Loader.consoleChannelId, message);
        }
    }

    /**
     * Internal: Directly send message to a given channel on Discord.
     *
     * @param channelId Channel ID
     * @param message   Message
     */
    static void sendMessageInternal(String channelId, String message) {
        if (Loader.jda != null) {
            TextChannel channel = Loader.jda.getTextChannelById(channelId);
            if (channel != null) {
                channel.sendMessage(message).queue();
            } else if (Loader.debug) {
                Loader.instance.getLogger().error("sendMessageInternal: TextChannel is null: " + channelId);
            }
        } else if (Loader.debug) {
            Loader.instance.getLogger().error("sendMessageInternal: JDA is null");
        }
    }

    /**
     * Set the channel topic of the default channel on Discord. Rate limits apply.
     *
     * @param topic New channel topic
     */
    public static void setTopic(String topic) {
        setTopic(Loader.channelId, topic);
    }

    /**
     * Set the channel topic of a given channel on Discord. Rate limits apply.
     *
     * @param channelId Channel ID
     * @param topic     New channel topic
     */
    public static void setTopic(String channelId, String topic) {
        if (channelId == null) {
            throw new IllegalArgumentException("Channel id cannot be null");
        }
        if (topic == null) {
            throw new IllegalArgumentException("topic cannot be null");
        }
        if (Loader.jda != null) {
            TextChannel channel = Loader.jda.getTextChannelById(channelId);
            if (channel != null) {
                channel.getManager().setTopic(topic).queue();
            } else if (Loader.debug) {
                Loader.instance.getLogger().error("setTopic: TextChannel is null: " + channelId);
            }
        } else if (Loader.debug) {
            Loader.instance.getLogger().error("setTopic: JDA is null");
        }
    }

    /**
     * Register your DiscordChatReceiver.
     *
     * @param receiver Your class that implements DiscordChatReceiver
     */
    @SuppressWarnings("unused")
    public static void registerReceiver(DiscordChatReceiver receiver) {
        DiscordListener.receivers.add(receiver);
    }

    /**
     * Get all registered DiscordChatReceivers.
     *
     * @return List of all registered DiscordChatReceivers
     */
    @SuppressWarnings("unused")
    public static List<DiscordChatReceiver> getReceiversList() {
        return DiscordListener.receivers;
    }

    /**
     * Get name of all players who have muted Discord chat in game.
     *
     * @return Modifiable list of all players who have Discord chat muted
     */
    @SuppressWarnings("unused")
    public static Set<String> getDiscordChatMutedPlayers() {
        return DiscordListener.chatMuted;
    }

    /**
     * Get JDA instance used to communicate with Discord.
     *
     * @return JDA instance
     */
    @SuppressWarnings("unused")
    public static JDA getJDA() {
        return Loader.jda;
    }
}
