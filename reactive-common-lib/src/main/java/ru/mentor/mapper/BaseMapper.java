package ru.mentor.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.admin.PageDetails;

/**
 * Базовый маппер для преобразования сущностей в DTO объекты.
 * Предоставляет методы для маппинга курсов, модулей и пользователей.
 */
@Component
@RequiredArgsConstructor
public class BaseMapper {

    /**
     * Преобразовать gRPC-объект в {@link PageRequest}
     *
     * @param pageDetails
     *         rRPC-объект {@link PageDetails}
     *
     * @return {@link PageRequest}
     */
    public PageRequest mapGrpcPageDetailsToPageRequest(PageDetails pageDetails) {
        return PageRequest.of(pageDetails.getPage(), pageDetails.getSize());
    }

    /**
     * Преобразовать gRPC-объект в {@link PageRequest}
     *
     * @param grpcPageRequest
     *         gRPC-объект {@link GrpcPageRequest}
     *
     * @return {@link PageRequest}
     */
    public PageRequest mapGrpcPageRequestToPageRequest(GrpcPageRequest grpcPageRequest) {
        return PageRequest.of(grpcPageRequest.getPageNumber(), grpcPageRequest.getPageSize());
    }

}
