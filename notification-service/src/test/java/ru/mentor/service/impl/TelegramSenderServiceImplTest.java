package ru.mentor.service.impl;

import feign.FeignException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import ru.mentor.config.TelegramBotProperties;
import ru.mentor.integration.telegram.TelegramApiClient;

@ExtendWith(MockitoExtension.class)
@ConditionalOnProperty(name = "application.notify.telegram.enable", havingValue = "true")
class TelegramSenderServiceImplTest {

    public final String TOKEN = "TEST_TOKEN";
    private final long CHAT_ID = 123L;
    private final String MESSAGE = "hello <b>world</b>";

    @Mock
    TelegramApiClient telegramApiClient;

    @Mock
    TelegramBotProperties botProperties;

    /**
     * @return экземпляр сервиса отправки сообщений в Telegram.
     */
    private TelegramSenderServiceImpl service() {
        return new TelegramSenderServiceImpl(botProperties, telegramApiClient);
    }

    /**
     * Проверяет, что метод sendMessage делегирует вызов
     * к Feign-клиенту с токеном из свойств.
     */
    @Test
    void sendMessage_delegatesToFeignWithTokenFromProps() {
        Mockito.when(botProperties.token()).thenReturn(TOKEN);
        service().sendMessage(CHAT_ID, MESSAGE, true);

        Mockito.verify(telegramApiClient).sendMessage(TOKEN, CHAT_ID, MESSAGE, true);
        Mockito.verifyNoMoreInteractions(telegramApiClient);
    }

    /**
     * Проверяет, что исключение, выбрасываемое Feign-клиентом,
     * корректно пропагируется выше.
     */
    @Test
    void sendMessage_whenFeignThrows_isPropagated() {
        Mockito.when(botProperties.token()).thenReturn(TOKEN);
        Mockito.doThrow(Mockito.mock(FeignException.class))
                .when(telegramApiClient)
                .sendMessage(TOKEN, CHAT_ID, MESSAGE, true);

        Assertions.assertThrows(FeignException.class, () -> service().sendMessage(CHAT_ID, MESSAGE, true));
    }

    /**
     * Проверяет, что метод sendMessage вызывает Feign-клиент
     * ровно один раз при переданных параметрах.
     */
    @Test
    void sendMessage_withChatId_callsFeignOnce() {
        Mockito.when(botProperties.token()).thenReturn(TOKEN);

        service().sendMessage(CHAT_ID, MESSAGE, true);

        Mockito.verify(telegramApiClient).sendMessage(TOKEN, CHAT_ID, MESSAGE, true);
        Mockito.verifyNoMoreInteractions(telegramApiClient);
    }
}
