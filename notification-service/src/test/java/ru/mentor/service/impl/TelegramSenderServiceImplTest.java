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

    public static final String TOKEN = "TEST_TOKEN";
    private static final long CHAT_ID = 123L;
    private static final String MESSAGE = "hello <b>world</b>";

    @Mock
    TelegramApiClient telegramApiClient;

    @Mock
    TelegramBotProperties botProperties;

    private TelegramSenderServiceImpl service() {
        return new TelegramSenderServiceImpl(botProperties, telegramApiClient);
    }

    @Test
    void sendMessage_delegatesToFeignWithTokenFromProps() {
        Mockito.when(botProperties.token()).thenReturn(TOKEN);
        service().sendMessage(CHAT_ID, MESSAGE, true);

        Mockito.verify(telegramApiClient).sendMessage(TOKEN, CHAT_ID, MESSAGE, true);
        Mockito.verifyNoMoreInteractions(telegramApiClient);
    }

    @Test
    void sendMessage_whenFeignThrows_isPropagated() {
        Mockito.when(botProperties.token()).thenReturn(TOKEN);
        Mockito.doThrow(Mockito.mock(FeignException.class))
                .when(telegramApiClient)
                .sendMessage(TOKEN, CHAT_ID, MESSAGE, true);

        Assertions.assertThrows(FeignException.class, () -> service().sendMessage(CHAT_ID, MESSAGE, true));
    }

    @Test
    void sendMessage_withChatId_callsFeignOnce() {
        Mockito.when(botProperties.token()).thenReturn(TOKEN);

        service().sendMessage(CHAT_ID, MESSAGE, true);

        Mockito.verify(telegramApiClient).sendMessage(TOKEN, CHAT_ID, MESSAGE, true);
        Mockito.verifyNoMoreInteractions(telegramApiClient);
    }
}
