package ua.ldv.discordbot.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;

@Configuration
class DiscordBotConfig {

    @Value("${discord.bot.token}")
    private String token;

    @Bean
    public JDABuilder jdaBuilder() {
        return JDABuilder.createDefault(token)
                .enableIntents(net.dv8tion.jda.api.requests.GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.playing("Type !help"))
                .addEventListeners(new BotListener());
    }

    @Bean
    public JDA jda() {
        try {
            return jdaBuilder()
                    .build()
                    .awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }
    }
}