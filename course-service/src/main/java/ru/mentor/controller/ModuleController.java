package ru.mentor.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для работы с курсами.
 */
@RestController
@RequestMapping("/module")
@RequiredArgsConstructor
@Tag(name = "Работа с модулями")
public class ModuleController {
}
