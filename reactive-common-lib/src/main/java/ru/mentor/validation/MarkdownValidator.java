package ru.mentor.validation;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.mentor.validation.exception.ValidationException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarkdownValidator {

    public Mono<Void> validate(byte[] content, String filename, String contentType) {
        return Mono.fromCallable(() -> {
            // Проверка на пустой контент
            if (content == null || content.length == 0) {
                throw new ValidationException("Файл не должен быть пустым");
            }

            // Проверка размера (максимум 5 МБ)
            if (content.length > 5 * 1024 * 1024) {
                throw new ValidationException("Файл слишком большой. Максимальный размер: 5 МБ");
            }

            // Проверка расширения
            if (filename == null || !filename.toLowerCase().endsWith(".md")) {
                throw new ValidationException("Файл должен иметь расширение .md");
            }

            // Проверка MIME-типа
            if (!"text/markdown".equals(contentType) &&
                    !"text/x-markdown".equals(contentType) &&
                    !"application/octet-stream".equals(contentType)) {
                throw new ValidationException("Неподдерживаемый тип файла. Ожидается Markdown (.md)");
            }

            return null;
        }).onErrorMap(ValidationException.class, e ->
                new StatusRuntimeException(Status.INVALID_ARGUMENT
                                                   .withDescription(e.getMessage()))
        ).then();
    }
}
