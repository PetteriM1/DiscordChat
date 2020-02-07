package discordchat;

import cn.nukkit.Server;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DiscordConsoleListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (!Main.config.getBoolean("discordConsole")) return;
        if (e.getAuthor() == null || e.getMember() == null || e.getAuthor().getId() == null || Main.jda == null || Main.jda.getSelfUser() == null || Main.jda.getSelfUser().getId() == null || e.getAuthor().equals(Main.jda.getSelfUser())) return;
        if (!e.getChannel().getId().equals(Main.consoleChannelId)) return;
        String message = e.getMessage().getContentStripped();
        if (message.length() > 1 && message.startsWith(Main.config.getString("commandPrefix"))) {
            if (!hasConsoleRole(e.getMember())) {
                API.sendToConsole(Main.config.getString("err_no_perm"));
                return;
            }
            Server.getInstance().getScheduler().scheduleTask(Main.instance, () -> Server.getInstance().dispatchCommand(Main.discordCommandSender, message.substring(1)));
        }
    }

    private boolean hasConsoleRole(Member m) {
        for (Role role : m.getRoles()) {
            if (role.getName().equals(Main.config.getString("consoleRole"))) {
                return true;
            }
        }
        return false;
    }
}
