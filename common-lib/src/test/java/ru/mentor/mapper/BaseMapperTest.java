package ru.mentor.mapper;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.mentor.common.GetAllModulesRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.Header;
import ru.mentor.common.PageDetails;
import ru.mentor.constant.Role;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.CourseTagEntity;
import ru.mentor.entity.CourseTagLinkEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;

class BaseMapperTest {

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, 4, 2, 12, 0);

    private final BaseMapper mapper = new BaseMapper();

    @Test
    void mapModule_whenContentNotNeeded_setsModuleContentNull() {
        ModuleEntity entity = ModuleEntity.builder()
                .id(1L)
                .moduleTitle("A")
                .moduleOrderNumber(1)
                .moduleContent("secret")
                .isActive(true)
                .createdAt(FIXED_TIME)
                .build();

        ModuleDto dto = mapper.mapModule(entity, false);

        Assertions.assertNull(dto.getModuleContent());
        Assertions.assertEquals("A", dto.getModuleTitle());
        Assertions.assertEquals(1, dto.getModuleOrderNumber());
    }

    @Test
    void mapModule_whenContentNeeded_includesModuleContent() {
        ModuleEntity entity = ModuleEntity.builder()
                .id(1L)
                .moduleTitle("A")
                .moduleOrderNumber(1)
                .moduleContent("body")
                .isActive(true)
                .createdAt(FIXED_TIME)
                .build();

        ModuleDto dto = mapper.mapModule(entity, true);

        Assertions.assertEquals("body", dto.getModuleContent());
    }

    @Test
    void mapModules_sortsByModuleOrderNumberAscending() {
        ModuleEntity second = ModuleEntity.builder()
                .id(2L)
                .moduleTitle("Second")
                .moduleOrderNumber(2)
                .moduleContent(null)
                .isActive(true)
                .createdAt(FIXED_TIME)
                .build();
        ModuleEntity first = ModuleEntity.builder()
                .id(1L)
                .moduleTitle("First")
                .moduleOrderNumber(1)
                .moduleContent(null)
                .isActive(true)
                .createdAt(FIXED_TIME)
                .build();

        List<ModuleDto> result = mapper.mapModules(List.of(second, first), false);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(1, result.get(0).getModuleOrderNumber());
        Assertions.assertEquals(2, result.get(1).getModuleOrderNumber());
    }

    @Test
    void mapCourse_whenAuthorNull_authorInDtoNull() {
        CourseEntity course = CourseEntity.builder()
                .id(10L)
                .courseTitle("Title")
                .description("Desc")
                .isActive(true)
                .createdAt(FIXED_TIME)
                .author(null)
                .modules(List.of())
                .courseTags(List.of())
                .build();

        CourseDto dto = mapper.mapCourse(course, null, false, false, false);

        Assertions.assertNull(dto.getAuthor());
        Assertions.assertNull(dto.getModules());
        Assertions.assertNull(dto.getTags());
    }

    @Test
    void mapCourse_whenModulesAndTagsRequested_mapsNestedLists() {
        UserEntity author = UserEntity.builder()
                .id(5L)
                .username("u")
                .password("p")
                .role(Role.MENTOR)
                .firstName("F")
                .lastName("L")
                .tgNickname("@n")
                .build();

        ModuleEntity mod = ModuleEntity.builder()
                .id(20L)
                .moduleTitle("M")
                .moduleOrderNumber(1)
                .moduleContent("c")
                .isActive(true)
                .createdAt(FIXED_TIME)
                .build();

        CourseTagEntity tag = CourseTagEntity.builder()
                .id(3L)
                .tagName("java")
                .isActive(true)
                .createdAt(FIXED_TIME)
                .build();
        CourseTagLinkEntity link = CourseTagLinkEntity.builder()
                .id(100L)
                .tag(tag)
                .createdAt(FIXED_TIME)
                .build();

        CourseEntity course = CourseEntity.builder()
                .id(10L)
                .courseTitle("Title")
                .description("Desc")
                .isActive(true)
                .createdAt(FIXED_TIME)
                .author(author)
                .modules(List.of(mod))
                .courseTags(List.of(link))
                .build();

        CourseDto dto = mapper.mapCourse(course, author, true, true, true);

        Assertions.assertEquals("Title", dto.getCourseTitle());
        Assertions.assertNotNull(dto.getAuthor());
        Assertions.assertEquals(5L, dto.getAuthor().getId());
        Assertions.assertEquals(1, dto.getModules().size());
        Assertions.assertEquals("c", dto.getModules().get(0).getModuleContent());
        Assertions.assertEquals(1, dto.getTags().size());
        Assertions.assertEquals("java", dto.getTags().get(0).getTagName());
    }

    @Test
    void mapCourses_delegatesToMapCourse() {
        UserEntity author = UserEntity.builder()
                .id(1L)
                .username("a")
                .password("p")
                .role(Role.USER)
                .firstName("A")
                .lastName("B")
                .tgNickname("@a")
                .build();

        CourseEntity course = CourseEntity.builder()
                .id(99L)
                .courseTitle("One")
                .description("d")
                .isActive(true)
                .createdAt(FIXED_TIME)
                .author(author)
                .modules(List.of())
                .courseTags(List.of())
                .build();

        List<CourseDto> list = mapper.mapCourses(List.of(course), false, false, false);

        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(99L, list.get(0).getId());
        Assertions.assertEquals(1L, list.get(0).getAuthor().getId());
    }

    @Test
    void mapUserDto_mapsCoreFields() {
        UserEntity entity = UserEntity.builder()
                .id(7L)
                .username("login")
                .password("pw")
                .role(Role.ADMIN)
                .firstName("Ivan")
                .lastName("Sidorov")
                .tgNickname("@ivan")
                .build();

        UserInfoDto dto = mapper.mapUserDto(entity);

        Assertions.assertEquals(7L, dto.getId());
        Assertions.assertEquals("login", dto.getUsername());
        Assertions.assertEquals(Role.ADMIN, dto.getRole());
        Assertions.assertEquals("Ivan", dto.getFirstName());
        Assertions.assertEquals("Sidorov", dto.getLastName());
        Assertions.assertEquals("@ivan", dto.getTgNickname());
    }

    @Test
    void mapUserEntity_mapsFromDtoIncludingTgChatId() {
        UserInfoDto dto = UserInfoDto.builder()
                .id(8L)
                .username("x")
                .role(Role.MENTOR)
                .firstName("A")
                .lastName("B")
                .tgNickname("@x")
                .tgChatId(12345L)
                .build();

        UserEntity entity = mapper.mapUserEntity(dto);

        Assertions.assertEquals(8L, entity.getId());
        Assertions.assertEquals("x", entity.getUsername());
        Assertions.assertEquals(Role.MENTOR, entity.getRole());
        Assertions.assertEquals(12345L, entity.getTgChatId());
    }

    @Test
    void constructGrpcPageRequest_threeArgs_setsHeaderAndPaging() {
        Header header = Header.newBuilder()
                .setRequestId("rq")
                .setNodeId("node")
                .setApiKey("key")
                .build();

        GrpcPageRequest grpc = mapper.constructGrpcPageRequest(header, 3, 20);

        Assertions.assertEquals(header, grpc.getHeader());
        Assertions.assertEquals(3, grpc.getPageNumber());
        Assertions.assertEquals(20, grpc.getPageSize());
    }

    @Test
    void constructGrpcPageRequest_fourArgs_setsSenderId() {
        Header header = Header.newBuilder().setRequestId("r").build();

        GrpcPageRequest grpc = mapper.constructGrpcPageRequest(header, 0, 10, 42L);

        Assertions.assertEquals(42L, grpc.getSenderId());
    }

    @Test
    void mapGrpcPageDetailsToPageRequest_buildsSpringPageRequest() {
        PageDetails details = PageDetails.newBuilder()
                .setPage(2)
                .setSize(15)
                .build();

        PageRequest pr = mapper.mapGrpcPageDetailsToPageRequest(details);

        Assertions.assertEquals(2, pr.getPageNumber());
        Assertions.assertEquals(15, pr.getPageSize());
    }

    @Test
    void mapGrpcPageRequestToPageRequest_usesPageNumberAndSize() {
        GrpcPageRequest grpc = GrpcPageRequest.newBuilder()
                .setPageNumber(5)
                .setPageSize(25)
                .build();

        PageRequest pr = mapper.mapGrpcPageRequestToPageRequest(grpc);

        Assertions.assertEquals(5, pr.getPageNumber());
        Assertions.assertEquals(25, pr.getPageSize());
    }

    @Test
    void constructGetAllModulesRequest_setsCourseIdAndHeader() {
        Header header = Header.newBuilder().setRequestId("id").build();

        GetAllModulesRequest req = mapper.constructGetAllModulesRequest(header, 77L);

        Assertions.assertEquals(header, req.getHeader());
        Assertions.assertEquals(77L, req.getCourseId());
    }
}
