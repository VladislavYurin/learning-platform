package ru.mentor.util;

import lombok.extern.slf4j.Slf4j;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import ru.mentor.exception.FileProcessingException;

/**
 * Утилита для конвертации текста из формата Markdown в HTML.
 * Этот класс использует библиотеку Flexmark для обработки Markdown-контента.
 */
@Slf4j
public class MarkdownConverter {

    private static final Parser parser = Parser.builder().build();

    private static final HtmlRenderer renderer = HtmlRenderer.builder().build();

    /**
     * Конвертирует строку в формате Markdown в HTML.
     *
     * @param markdown Строка, содержащая текст в формате Markdown.
     * @return Строка, содержащая преобразованный HTML-код.
     * @throws FileProcessingException Если возникает ошибка при конвертации Markdown в HTML.
     */
    public static String markdownToHtml(String markdown) {
        try {
            Node document = parser.parse(markdown);
            return renderer.render(document);
        } catch (Exception e) {
            log.error("Ошибка конвертации Markdown в HTML", e);
            throw new FileProcessingException("Не удалось конвертировать Markdown в HTML");
        }
    }

}
