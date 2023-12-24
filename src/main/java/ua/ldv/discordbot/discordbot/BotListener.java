package ua.ldv.discordbot.discordbot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;

@Component
public class BotListener extends ListenerAdapter {
    @Value("${discord.bot.server-ip}")
    String ipAddress;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();

        if (message.equalsIgnoreCase("!serverstatus")) {

            String reply;

            try {
                InetAddress address = InetAddress.getByName(ipAddress);
                if (address.isReachable(5000)) {
                    reply = "Сервер доступний.";
                } else {
                    reply = "Сервер недоступний.";
                }
            } catch (IOException e) {
                reply = "Помилка при спробі доступу до сервера: " + e.getMessage();
            }

            event.getChannel().sendMessage(reply).queue();
        }
    }
}
