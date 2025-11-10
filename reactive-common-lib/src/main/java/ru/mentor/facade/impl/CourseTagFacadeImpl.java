package ru.mentor.facade.impl;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.common.CreateCourseTagGrpcRequest;
import ru.mentor.common.DeleteCourseTagResponse;
import ru.mentor.common.ListCourseTagsResponse;
import ru.mentor.entity.CourseTagEntity;
import ru.mentor.facade.CourseTagFacade;
import ru.mentor.mapper.TagMapper;
import ru.mentor.repository.CourseTagRepository;

/**
 * Фасад для работы с курсами.
 * Абстракция для работы со связанными таблицами в реактивных репозиториях и для маппинга.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseTagFacadeImpl implements CourseTagFacade {

    private final CourseTagRepository courseTagRepository;

    private final TagMapper tagMapper;

    /**
     * Создает новый тег
     * @param request - DTO запроса с данными создаваемого тега
     * @return - DTO созданного тега
     */
    @Override
    public Mono<CourseTagResponse> createCourseTag(CreateCourseTagGrpcRequest request) {
        CourseTagEntity courseTagEntity =
                CourseTagEntity.builder()
                .tagName(request.getName())
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .build();

        return courseTagRepository.save(courseTagEntity)
                                  .map(savedTag ->
                                               tagMapper.toGrpcTagResponse(courseTagEntity));
    }

    /**
     * Удаляет тег по его ID
     *
     * @param tagId - ID тега в таблице course_tags
     *
     * @return DTO с пустым ответом
     */
    @Override
    public Mono<DeleteCourseTagResponse> deleteCourseTag(Long tagId) {
        return courseTagRepository.deleteById(tagId)
                .thenReturn(DeleteCourseTagResponse.newBuilder().build());
    }

    /**
     * Находит тег по его ID
     *
     * @param tagId - ID тега в таблице course_tags
     *
     * @return - DTO с данными найденного тега
     */
    @Override
    public Mono<CourseTagResponse> getTagById(Long tagId) {
        return courseTagRepository.findByIdOrThrow(tagId)
                .map(tagMapper::toGrpcTagResponse);
    }

    /**
     * Возвращает все теги
     *
     * @return Mono со списком DTO всех тегов
     */
    @Override
    public Mono<ListCourseTagsResponse> getAllTags() {
        Flux<CourseTagEntity> courseTagEntityFlux = courseTagRepository.findAll();
        return courseTagEntityFlux.collectList()
                           .map(tagMapper::toGrpcTagsListResponse);

    }
}
