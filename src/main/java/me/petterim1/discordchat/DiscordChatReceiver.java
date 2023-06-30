package me.petterim1.discordchat;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface DiscordChatReceiver {

    /**
     * Called on JDA GuildMessageReceivedEvent.
     * https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/events/message/guild/GuildMessageReceivedEvent.html
     *
     * @param event GuildMessageReceivedEvent
     */
    void receive(GuildMessageReceivedEvent event);
}
