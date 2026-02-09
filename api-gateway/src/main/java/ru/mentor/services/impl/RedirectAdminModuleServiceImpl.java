package ru.mentor.services.impl;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.Header;
import ru.mentor.common.ModuleResponse;
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
public class RedirectAdminModuleServiceImpl implements RedirectAdminModuleService {

    private final AdminModuleServiceGrpcClient moduleGrpcClient;

    private final UserService userService;

    private final AdminModuleMapper moduleMapper;

    private final BaseMapper baseMapper;

    private final HeaderFactory headerFactory;

    public RedirectAdminModuleServiceImpl(
            AdminModuleServiceGrpcClient moduleGrpcClient,
            UserService userService,
            @Qualifier("adminModuleMapperImpl") AdminModuleMapper moduleMapper,
            @Qualifier("baseMapperImpl") BaseMapper baseMapper,
            HeaderFactory headerFactory) {
        this.moduleGrpcClient = moduleGrpcClient;
        this.userService = userService;
        this.moduleMapper = moduleMapper;
        this.baseMapper = baseMapper;
        this.headerFactory = headerFactory;
    }

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

        String requestId = UUID.randomUUID().toString();
        Header header = headerFactory.create(requestId);
        Long adminId = userService.getCurrentUserId();
        log.info(
                "[ requestId = {} ] Получен запрос на извлечение модуля [ ID = {} ] от администратора [ ID = {} ]",
                requestId,
                moduleId,
                adminId
        );

        GetModuleRequest grpcRequest = moduleMapper.toGetModuleRequest(header, moduleId);
        ModuleResponse grpcModuleResponse = moduleGrpcClient.getModule(grpcRequest);
        return moduleMapper.moduleResponseToModuleDto(grpcModuleResponse);
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

        String requestId = UUID.randomUUID().toString();
        Header header = headerFactory.create(requestId);
        Long adminId = userService.getCurrentUserId();
        log.info(
                "[ requestId = {} ] Получен запрос на извлечение всех модулей от администратора [ ID = {} ]",
                requestId,
                adminId
        );

        GrpcPageRequest pageRequest = baseMapper.toGrpcPageRequest(
                header,
                pageNumber,
                pageSize
        );
        AllModulesResponse allModules = moduleGrpcClient.getAllModules(pageRequest);
        return moduleMapper.allModulesResponseToModuleDtoPage(allModules);
    }

}
