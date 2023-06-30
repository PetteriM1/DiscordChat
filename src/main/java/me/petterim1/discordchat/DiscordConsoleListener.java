package me.petterim1.discordchat;

import cn.nukkit.Server;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class DiscordConsoleListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent e) {
        if (!Loader.config.getBoolean("discordConsole")) {
            return;
        }
        if (e.getMember() == null || Loader.jda == null || e.getAuthor().equals(Loader.jda.getSelfUser())) {
            return;
        }
        if (!e.getChannel().getId().equals(Loader.consoleChannelId)) {
            return;
        }
        String message = e.getMessage().getContentStripped();
        String prefix = Loader.config.getString("commandPrefix");
        if (message.length() > prefix.length() && message.startsWith(prefix)) {
            if (!hasConsoleRole(e.getMember())) {
                API.sendToConsole(Loader.config.getString("err_no_perm"));
                return;
            }
            String cmd = message.substring(prefix.length());
            if (Loader.config.getBoolean("logConsoleCommands")) {
                Loader.instance.getLogger().info(e.getMember().getEffectiveName() + " executed a console command: " + cmd);
            }
            Server.getInstance().getScheduler().scheduleTask(Loader.instance, () -> Server.getInstance().dispatchCommand(Loader.discordCommandSender, cmd));
        }
    }

    private boolean hasConsoleRole(Member m) {
        for (Role role : m.getRoles()) {
            if (role.getName().equals(Loader.config.getString("consoleRole"))) {
                return true;
            }
        }
        return false;
    }
}
