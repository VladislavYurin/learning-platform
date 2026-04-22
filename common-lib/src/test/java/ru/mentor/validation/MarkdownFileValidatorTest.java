package ru.mentor.validation;

import jakarta.validation.Payload;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class MarkdownFileValidatorTest {

    private final MarkdownFileValidator validator = new MarkdownFileValidator();

    @BeforeEach
    void setUp() {
        validator.initialize(defaultAnnotation());
    }

    @Test
    void isValid_validMdFile_returnsTrue() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "notes.md",
                "text/markdown",
                "# Title".getBytes(StandardCharsets.UTF_8));

        boolean ok = validator.isValid(file, Mockito.mock(jakarta.validation.ConstraintValidatorContext.class));

        Assertions.assertTrue(ok);
    }

    @Test
    void isValid_nullFile_returnsFalse() {
        ConstraintValidatorContextMock ctx = new ConstraintValidatorContextMock();

        Assertions.assertFalse(validator.isValid(null, ctx.context));
    }

    @Test
    void isValid_wrongExtension_returnsFalse() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "notes.txt",
                "text/markdown",
                "x".getBytes(StandardCharsets.UTF_8));

        ConstraintValidatorContextMock ctx = new ConstraintValidatorContextMock();

        Assertions.assertFalse(validator.isValid(file, ctx.context));
    }

    @Test
    void isValid_wrongContentType_returnsFalse() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "notes.md",
                "image/png",
                "x".getBytes(StandardCharsets.UTF_8));

        ConstraintValidatorContextMock ctx = new ConstraintValidatorContextMock();

        Assertions.assertFalse(validator.isValid(file, ctx.context));
    }

    @Test
    void isValid_fileTooLarge_returnsFalse() {
        MarkdownFileValidator smallLimit = new MarkdownFileValidator();
        smallLimit.initialize(new ValidMarkdownFile() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return ValidMarkdownFile.class;
            }

            @Override
            public String message() {
                return "";
            }

            @Override
            public Class<?>[] groups() {
                return new Class<?>[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                @SuppressWarnings("unchecked")
                Class<? extends Payload>[] p = new Class[0];
                return p;
            }

            @Override
            public long maxSize() {
                return 2L;
            }

            @Override
            public String[] allowedTypes() {
                return new String[]{"text/markdown", "text/x-markdown", "application/octet-stream"};
            }
        });

        MultipartFile file = new MockMultipartFile(
                "file",
                "big.md",
                "text/markdown",
                "abc".getBytes(StandardCharsets.UTF_8));

        ConstraintValidatorContextMock ctx = new ConstraintValidatorContextMock();

        Assertions.assertFalse(smallLimit.isValid(file, ctx.context));
    }

    private static ValidMarkdownFile defaultAnnotation() {
        return new ValidMarkdownFile() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return ValidMarkdownFile.class;
            }

            @Override
            public String message() {
                return "";
            }

            @Override
            public Class<?>[] groups() {
                return new Class<?>[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                @SuppressWarnings("unchecked")
                Class<? extends Payload>[] p = new Class[0];
                return p;
            }

            @Override
            public long maxSize() {
                return 5L * 1024 * 1024;
            }

            @Override
            public String[] allowedTypes() {
                return new String[]{"text/markdown", "text/x-markdown", "application/octet-stream"};
            }
        };
    }

    /**
     * Minimal mock so {@link MarkdownFileValidator} can add violations on failure paths.
     */
    private static final class ConstraintValidatorContextMock {
        private final jakarta.validation.ConstraintValidatorContext context =
                Mockito.mock(jakarta.validation.ConstraintValidatorContext.class);

        private ConstraintValidatorContextMock() {
            jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder builder =
                    Mockito.mock(jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.class);
            Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(builder);
            Mockito.when(builder.addConstraintViolation()).thenReturn(null);
            Mockito.doNothing().when(context).disableDefaultConstraintViolation();
        }
    }
}
