package me.petterim1.discordchat;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageQueue implements Runnable {

    static Queue<String> console = new ConcurrentLinkedQueue<>();
    static Queue<String> defaultChat = new ConcurrentLinkedQueue<>();
    static Map<String, Queue<String>> customChat = new ConcurrentHashMap<>();

    @Override
    public void run() {
        StringBuilder current = new StringBuilder();
        while (true) {
            String message = console.poll();
            if (message == null) {
                break;
            }
            current.append(message).append('\n');
        }
        String send = current.toString();
        if (send.length() > 1999) {
            int index = 0;
            while (send.length() > 1999) {
                API.sendMessageInternal(Loader.consoleChannelId, send.substring(index, index + 1998));
                index = index + 1998;
                send = send.substring(index);
            }
        }
        if (!send.isEmpty()) API.sendMessageInternal(Loader.consoleChannelId, send);
        current.setLength(0);
        while (true) {
            String message = defaultChat.poll();
            if (message == null) {
                break;
            }
            current.append(message).append('\n');
        }
        send = current.toString();
        if (send.length() > 1999) {
            int index = 0;
            while (send.length() > 1999) {
                API.sendMessageInternal(Loader.channelId, send.substring(index, index + 1998));
                index = index + 1998;
                send = send.substring(index);
            }
        }
        if (!send.isEmpty()) API.sendMessageInternal(Loader.channelId, send);
        if (!customChat.isEmpty()) {
            StringBuilder currentChannel = new StringBuilder();
            customChat.forEach((channel, queue) -> {
                while (true) {
                    String message = queue.poll();
                    if (message == null) {
                        break;
                    }
                    currentChannel.append(message).append('\n');
                }
                String currentSend = currentChannel.toString();
                if (currentSend.length() > 1999) {
                    int index = 0;
                    while (currentSend.length() > 1999) {
                        API.sendMessageInternal(channel, currentSend.substring(index, index + 1998));
                        index = index + 1998;
                        currentSend = currentSend.substring(index);
                    }
                }
                if (!currentSend.isEmpty()) API.sendMessageInternal(channel, currentSend);
            });
        }
    }
}
