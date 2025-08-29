package ru.mentor.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для валидации Markdown файлов.
 * Используется для проверки корректности загружаемых Markdown файлов
 * по критериям типа содержимого, размера и расширения.
 *
 * По умолчанию проверяет файлы с расширением .md, максимальным размером 5 МБ
 * и разрешенными типами содержимого: text/markdown, text/x-markdown, application/octet-stream.
 */
@Documented
@Constraint(validatedBy = MarkdownFileValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMarkdownFile {

    /**
     * Сообщение об ошибке по умолчанию.
     *
     * @return сообщение об ошибке
     */
    String message() default "Invalid markdown file";

    /**
     * Группы валидации.
     *
     * @return массив групп валидации
     */
    Class<?>[] groups() default {};

    /**
     * Дополнительная информация о нагрузке.
     *
     * @return массив классов нагрузки
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Максимально допустимый размер файла в байтах.
     * По умолчанию 5 МБ.
     *
     * @return максимальный размер файла в байтах
     */
    long maxSize() default 5 * 1024 * 1024;

    /**
     * Массив разрешенных типов содержимого файла.
     * По умолчанию разрешены: text/markdown, text/x-markdown, application/octet-stream.
     *
     * @return массив разрешенных типов содержимого
     */
    String[] allowedTypes() default {
            "text/markdown",
            "text/x-markdown",
            "application/octet-stream"
    };

}
