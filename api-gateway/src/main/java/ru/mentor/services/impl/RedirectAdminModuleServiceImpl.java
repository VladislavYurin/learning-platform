package ru.mentor.services.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.mentor.admin.AllModulesResponse;
import ru.mentor.admin.GetModuleRequest;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.admin.ModuleResponse;
import ru.mentor.dto.ModuleDto;
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

    /**
     * Возвращает модуль с указанным ID.
     * @param moduleId ID модуля
     *
     * @return {@link ModuleDto}
     */
    @Override
    public ModuleDto getModuleById(Long moduleId) {

        String requestId = UUID.randomUUID().toString();
        Long adminId = userService.getCurrentUserId();
        log.info(
                "[ rqUID = {} ] Получен запрос на извлечение модуля [ ID = {} ] от администратора [ ID = {} ]",
                requestId,
                moduleId,
                adminId
        );

        GetModuleRequest grpcRequest = moduleMapper.constructGetModuleRequest(requestId, moduleId);
        ModuleResponse grpcModuleResponse = moduleGrpcClient.getModule(grpcRequest);
        return moduleMapper.mapGrpcModuleResponseToModuleDto(grpcModuleResponse);
    }

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
    @Override
    public Page<ModuleDto> getAllModules(int pageNumber, int pageSize) {

        String requestId = UUID.randomUUID().toString();
        Long adminId = userService.getCurrentUserId();
        log.info(
                "[ rqUID = {} ] Получен запрос на извлечение всех модулей от администратора [ ID = {} ]",
                requestId,
                adminId
        );

        GrpcPageRequest grpcPageRequest = baseMapper.constructGrpcPageRequest(requestId, pageNumber, pageSize);
        AllModulesResponse allModules = moduleGrpcClient.getAllModules(grpcPageRequest);
        return moduleMapper.mapGrpcAllModulesResponseToModuleDtoPage(allModules);
    }

}
