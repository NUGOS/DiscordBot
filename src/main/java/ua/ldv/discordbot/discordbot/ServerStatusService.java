package ua.ldv.discordbot.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ServerStatusService {

    private final JDA jda;
    private final String serverIp;
    private final String channelId;
    private boolean wasOffline = false;

    @Autowired
    public ServerStatusService(JDA jda, @Value("${discord.bot.server-ip}")
                                String serverIp, @Value("${discord.bot.channel-id}") String channelId) {
        this.jda = jda;
        this.serverIp = serverIp;
        this.channelId = channelId;
    }

    @PostConstruct
    public void init() {
        new Thread(() -> {
            try {
                // Затримка, щоб дати час JDA повністю підключитися
                Thread.sleep(5000);
                checkServerStatus();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::checkServerStatus, 0, 60, TimeUnit.SECONDS);
    }

    private void checkServerStatus() {
        try {
            InetAddress address = InetAddress.getByName(serverIp);
            boolean isReachable = address.isReachable(5000);

            if (isReachable && wasOffline) {
                sendStatusMessage("Сервер " + serverIp + " знову онлайн!");
                wasOffline = false;
            } else if (!isReachable && !wasOffline) {
                sendStatusMessage("Сервер " + serverIp + " тепер офлайн!");
                wasOffline = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendStatusMessage(String message) {
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) {
            channel.sendMessage(message).queue();
        }
    }
}
