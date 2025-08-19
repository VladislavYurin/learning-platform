package ru.mentor.integration.telegram;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mentor.config.TelegramApiConfig;

/**
 * Feign-клиент для REST-взаимодействия с API Telegram
 */
@FeignClient(
        name = "telegramApi",
        url = "${telegram.bot.api-url}",
        configuration = TelegramApiConfig.class
)
public interface TelegramApiClient {

    /**
     * Отправляет сообщение в Telegram-чат.
     *
     * @param token         токен API для доступа к вашему Telegram-боту.
     * @param chatId       идентификатор чата, в который будет отправлено сообщение.
     * @param text          текст сообщения, которое нужно отправить.
     * @param disablePreview флаг, указывающий, нужно ли отключить предварительный просмотр веб-страниц в сообщении.
     * @return ответ от API Telegram.
     */
    @PostMapping(path = "/bot{token}/sendMessage",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String sendMessage(@PathVariable("token") String token,
                       @RequestParam("chat_id") Long chatId,
                       @RequestParam("text") String text,
                       @RequestParam("disable_web_page_preview") boolean disablePreview);
}
