package ru.mentor.integration.telegram;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mentor.config.TelegramApiConfig;

@FeignClient(
        name = "telegramApi",
        url = "${telegram.bot.api-url}",
        configuration = TelegramApiConfig.class
)
@ConditionalOnProperty(name = "application.notify.telegram.enable", havingValue = "true")
public interface TelegramApiClient {

    @PostMapping(path = "/bot{token}/sendMessage",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String sendMessage(@PathVariable("token") String token,
                       @RequestParam("chat_id") Long chatId,
                       @RequestParam("text") String text,
                       @RequestParam("disable_web_page_preview") boolean disablePreview);
}
