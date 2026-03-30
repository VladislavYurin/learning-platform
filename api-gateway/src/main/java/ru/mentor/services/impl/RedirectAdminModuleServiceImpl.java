package ru.mentor.services.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.Header;
import ru.mentor.common.ModuleResponse;
import ru.mentor.constant.MdcKeys;
import ru.mentor.dto.ModuleDto;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.AdminModuleServiceGrpcClient;
import ru.mentor.mapper.AdminModuleMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.services.RedirectAdminModuleService;
import ru.mentor.services.UserService;

/**
 * Редирект сервис управления модулями. Необходимы права администратора.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedirectAdminModuleServiceImpl implements RedirectAdminModuleService {

    private final AdminModuleServiceGrpcClient moduleGrpcClient;

    private final UserService userService;

    private final AdminModuleMapper moduleMapper;

    private final BaseMapper baseMapper;

    private final HeaderFactory headerFactory;

    /**
     * Возвращает модуль с указанным ID.
     *
     * @param moduleId
     *         ID модуля
     *
     * @return {@link ModuleDto}
     */
    @Override
    public ModuleDto getModuleById(Long moduleId) {

        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Long userId = userService.getCurrentUserId();
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] Получен запрос на извлечение модуля [moduleId={}].",
                userId,
                moduleId
        );

        GetModuleRequest grpcRequest = moduleMapper.constructGetModuleRequest(header, userId, moduleId);

        try {
            ModuleResponse grpcModuleResponse = moduleGrpcClient.getModule(grpcRequest);

            log.debug(
                    "[userId={}] Успешно получен ответ от module-service на извлечение модуля [moduleId={}].",
                    userId,
                    moduleId
            );

            return moduleMapper.mapGrpcModuleResponseToModuleDto(grpcModuleResponse);
        } catch (Exception e) {
            log.error(
                    "[userId={}] Ошибка при вызове module-service во время извлечения модуля [moduleId={}].",
                    userId,
                    moduleId,
                    e
            );
            throw e;
        }
    }

    /**
     * Возвращает страницу модулей.
     *
     * @param pageNumber
     *         Номер страницы
     * @param pageSize
     *         Размер страницы
     *
     * @return объект {@link Page}, содержащий объекты {@link ModuleDto}
     */
    @Override
    public Page<ModuleDto> getAllModules(Integer pageNumber, Integer pageSize) {

        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Long userId = userService.getCurrentUserId();
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] [pageNumber={}] [pageSize={}] Получен запрос на извлечение всех модулей.",
                userId,
                pageNumber,
                pageSize
        );

        GrpcPageRequest pageRequest = baseMapper.constructGrpcPageRequest(
                header,
                pageNumber,
                pageSize,
                userId
        );

        try {
            AllModulesResponse allModules = moduleGrpcClient.getAllModules(pageRequest);

            log.debug(
                    "[userId={}] [pageNumber={}] [pageSize={}] Успешно получен ответ от module-service на извлечение всех модулей.",
                    userId,
                    pageNumber,
                    pageSize
            );

            return moduleMapper.mapGrpcAllModulesResponseToModuleDtoPage(allModules);
        } catch (Exception e) {
            log.error(
                    "[userId={}] [pageNumber={}] [pageSize={}] Ошибка при вызове module-service во время извлечения всех модулей.",
                    userId,
                    pageNumber,
                    pageSize,
                    e
            );
            throw e;
        }
    }
}
