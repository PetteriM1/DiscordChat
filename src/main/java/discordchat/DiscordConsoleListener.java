package discordchat;

import cn.nukkit.Server;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DiscordConsoleListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (!DiscordChatMain.config.getBoolean("discordConsole")) return;
        if (e.getAuthor() == null || e.getMember() == null || e.getAuthor().getId() == null || DiscordChatMain.jda == null || DiscordChatMain.jda.getSelfUser() == null || DiscordChatMain.jda.getSelfUser().getId() == null || e.getAuthor().equals(DiscordChatMain.jda.getSelfUser())) return;
        if (!e.getChannel().getId().equals(DiscordChatMain.consoleChannelId)) return;
        String message = e.getMessage().getContentStripped();
        if (message.length() > 1 && message.startsWith(DiscordChatMain.config.getString("commandPrefix"))) {
            Server.getInstance().getScheduler().scheduleTask(DiscordChatMain.instance, () -> Server.getInstance().dispatchCommand(DiscordChatMain.discordCommandSender, message.substring(1)));
        }
    }
}
