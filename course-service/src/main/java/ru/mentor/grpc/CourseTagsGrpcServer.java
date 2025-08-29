package ru.mentor.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.mentor.dto.tag.CourseTagDto;
import ru.mentor.grpc.tags.Empty;
import ru.mentor.grpc.tags.ListTagsResponse;
import ru.mentor.grpc.tags.Tag;
import ru.mentor.grpc.tags.TagsServiceGrpc;
import ru.mentor.mapper.CourseTagMapper;
import ru.mentor.repository.CourseTagRepository;

import java.util.List;

/**
 * gRPC-сервер для работы с тегами.
 *
 * <p>Текущая реализация поддерживает получение списка всех тегов.</p>
 */
@GrpcService
@RequiredArgsConstructor
public class CourseTagsGrpcServer extends TagsServiceGrpc.TagsServiceImplBase {

    private final CourseTagMapper mapper;

    private final CourseTagRepository tagRepository;


    /**
     * Возвращает список всех тегов.
     *
     * @param request          пустой запрос {@link Empty}
     * @param responseObserver поток для отправки ответа клиенту
     */
    @Override
    public void listTags(Empty request, StreamObserver<ListTagsResponse> responseObserver) {

        ListTagsResponse.Builder response = ListTagsResponse.newBuilder();

        List<CourseTagDto> tags = tagRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();

        tags.forEach(tagDto ->
                response.addTags(
                        Tag.newBuilder()
                                .setId(tagDto.getId())
                                .setName(tagDto.getTagName() == null ? "" : tagDto.getTagName())
                                .build()
                )
        );

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
