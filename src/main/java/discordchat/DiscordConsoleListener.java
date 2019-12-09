package discordchat;

import cn.nukkit.Server;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DiscordConsoleListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (!DiscordChatMain.config.getBoolean("discordConsole")) return;
        if (e.getAuthor() == null || e.getMember() == null || e.getAuthor().getId() == null || DiscordChatMain.jda == null || DiscordChatMain.jda.getSelfUser() == null || DiscordChatMain.jda.getSelfUser().getId() == null || e.getAuthor().equals(DiscordChatMain.jda.getSelfUser())) return;
        if (!e.getChannel().getId().equals(DiscordChatMain.consoleChannelId)) return;
        if(!checkRole(e.getMember())) return;
        String message = e.getMessage().getContentStripped();
        if (message.length() > 1 && message.startsWith(DiscordChatMain.config.getString("commandPrefix"))) {
            Server.getInstance().getScheduler().scheduleTask(DiscordChatMain.instance, () -> Server.getInstance().dispatchCommand(DiscordChatMain.discordCommandSender, message.substring(1)));
        }
    }

    private boolean checkRole(Member member) {
        if(DiscordChatMain.consoleRole == null || DiscordChatMain.consoleRole.length() <= 0) {
            DiscordChatMain.instance.getServer().getLogger().warning("Discord Role not set in config.yml, please set required role");
            return false;
        }
        for (Role r : member.getRoles()) {
            if (r.getName().compareTo(DiscordChatMain.consoleRole) == 0) return true;
        }
        return false;
    }
}
