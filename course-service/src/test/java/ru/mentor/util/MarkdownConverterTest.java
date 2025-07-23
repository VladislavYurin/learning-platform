package ru.mentor.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.util.ResourceUtils;

public class MarkdownConverterTest {

    @Test
    public void markdownToHtml_correctMdFileAsParam_returnCorrectHTML()
            throws IOException {

        Path filePath = ResourceUtils.getFile("classpath:test1.md").toPath();
        String markdownContent = Files.readString(filePath);

        String htmlResult = MarkdownConverter.markdownToHtml(markdownContent);

        Assertions.assertNotNull(htmlResult);
        Assertions.assertTrue(htmlResult.contains("<h1>"), "Должен содержать HTML-заголовок");
        Assertions.assertTrue(htmlResult.contains("<p>"), "Должен содержать HTML-параграф");
        String expectedHtmlFragment = "<p>Резюме и легенду составим для успешного прохождения фильтров HR.</p>";
        Assertions.assertTrue(htmlResult.contains(expectedHtmlFragment));

    }

}
