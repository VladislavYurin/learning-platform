package ru.mentor.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class MarkdownFileValidator implements
        ConstraintValidator<ValidMarkdownFile, MultipartFile> {

    private long maxSize;
    private String[] allowedTypes;

    @Override
    public void initialize(ValidMarkdownFile constraint) {
        this.maxSize = constraint.maxSize();
        this.allowedTypes = constraint.allowedTypes();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            addConstraintViolation(context, "Файл не должен быть пустым");
            return false;
        }

        if (!isContentTypeValid(file.getContentType())) {
            addConstraintViolation(context,
                                   "Неподдерживаемый тип файла. Разрешены: " + String.join(
                                           ", ",
                                           allowedTypes
                                   )
            );
            return false;
        }

        if (file.getSize() > maxSize) {
            addConstraintViolation(context,
                                   String.format(
                                           "Файл слишком большой. Максимальный размер: %d MB",
                                           maxSize / (1024 * 1024)
                                   )
            );
            return false;
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".md")) {
            addConstraintViolation(context, "Файл должен иметь расширение .md");
            return false;
        }

        return true;
    }

    private boolean isContentTypeValid(String contentType) {
        if (contentType == null) {
            return false;
        }
        for (String allowedType : allowedTypes) {
            if (allowedType.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
               .addConstraintViolation();
    }

}
