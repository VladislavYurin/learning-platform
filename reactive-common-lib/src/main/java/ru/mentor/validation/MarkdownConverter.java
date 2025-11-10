package ru.mentor.validation;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownConverter {
    private static final Parser PARSER = Parser.builder().build();
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder().build();

    public static String markdownToHtml(String markdown) {
        Node document = PARSER.parse(markdown);
        return RENDERER.render(document);
    }
}
