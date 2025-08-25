package ru.mentor.services;

import org.springframework.data.domain.Page;
import ru.mentor.dto.ModuleDto;

/**
 * Редирект сервис управления модулями. Необходимы права администратора.
 */
public interface RedirectAdminModuleService {

    /**
     * Возвращает модуль с указанным ID.
     * @param moduleId ID модуля
     *
     * @return {@link ModuleDto}
     */
    ModuleDto getModuleById(Long moduleId);

    /**
     * Возвращает страницу модулей.
     *
     * @param pageNumber
     *         номер страницы
     *
     * @param pageSize
     *         размер страницы
     *
     * @return объект {@link Page}, содержащий объекты {@link ModuleDto}
     */
    Page<ModuleDto> getAllModules(int pageNumber, int pageSize);

}
