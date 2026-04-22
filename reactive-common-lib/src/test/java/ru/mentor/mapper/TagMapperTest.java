package ru.mentor.mapper;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.common.ListCourseTagsResponse;
import ru.mentor.entity.CourseTagEntity;

class TagMapperTest {

    private final TagMapper mapper = new TagMapper();

    @Test
    void toGrpcTagResponse_mapsFields() {
        LocalDateTime created = LocalDateTime.of(2026, 4, 2, 10, 0);
        CourseTagEntity entity = CourseTagEntity.builder()
                .id(5L)
                .tagName("spring")
                .isActive(true)
                .createdAt(created)
                .build();

        CourseTagResponse grpc = mapper.toGrpcTagResponse(entity);

        Assertions.assertEquals(5L, grpc.getId());
        Assertions.assertEquals("spring", grpc.getName());
        Assertions.assertTrue(grpc.getIsActive());
        Assertions.assertEquals(
                created.toEpochSecond(java.time.ZoneOffset.UTC),
                grpc.getCreatedAt().getSeconds());
    }

    @Test
    void toGrpcTagsListResponse_mapsEachTag() {
        CourseTagEntity t1 = CourseTagEntity.builder()
                .id(1L)
                .tagName("a")
                .isActive(true)
                .createdAt(LocalDateTime.of(2026, 1, 1, 0, 0))
                .build();
        CourseTagEntity t2 = CourseTagEntity.builder()
                .id(2L)
                .tagName("b")
                .isActive(false)
                .createdAt(LocalDateTime.of(2026, 1, 2, 0, 0))
                .build();

        ListCourseTagsResponse list = mapper.toGrpcTagsListResponse(List.of(t1, t2));

        Assertions.assertEquals(2, list.getTagsCount());
        Assertions.assertEquals("a", list.getTags(0).getName());
        Assertions.assertEquals("b", list.getTags(1).getName());
    }
}
