package ru.mentor.services;

import org.springframework.web.multipart.MultipartFile;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.front.CreateModuleRequest;
import ru.mentor.dto.front.UpdateModuleRequest;

/**
 * Сервис редиректов/интеграции для операций с модулями курса.
 * <p>
 *     Инкапсулирует обращение к внешнему сервису модулей и скрывает детали
 *     транспортного слоя/авторизации. Используется контроллерами для создания,
 *     получения, импорта и удаления модулей.
 * </p>
 */
public interface RedirectModuleService {

    /**
     * Создаёт новый модуль в составе курса.
     * @param request данные для создания модуля
     * @return созданный модуль
     */
    ModuleDto createModule(CreateModuleRequest request);

    /**
     * Выполняет полное обновление модуля.
     * @param request запрос на обновление модуля
     * @return DTO обновленного модуля
     */
    ModuleDto updateModule(UpdateModuleRequest request);

    /**
     * Возвращает модуль по идентификатору курса и модуля.
     * @param courseId идентификатор курса
     * @param moduleId идентификатор модуля внутри курса
     * @return найденный модуль
     */
    ModuleDto getModuleById(Long courseId, Long moduleId);

    /**
     * Импортирует модуль из загруженного файла (например, Markdown) с дополнительными параметрами запроса.
     * @param request параметры создаваемого модуля
     * @param file файл с содержимым модуля
     * @return созданный модуль
     */
    ModuleDto importModuleFromFile(CreateModuleRequest request, MultipartFile file);

    /**
     * Удаляет модуль из курса.
     * @param courseId идентификатор курса
     * @param moduleId идентификатор модуля внутри курса
     */
    void deleteModule(Long courseId, Long moduleId);

}
