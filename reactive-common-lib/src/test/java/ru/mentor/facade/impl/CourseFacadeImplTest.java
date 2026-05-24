package ru.mentor.facade.impl;

import java.util.List;
import java.util.function.Function;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.mentor.cache.CacheAdapter;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.CreateCourseGrpcRequest;
import ru.mentor.common.DeleteCourseResponse;
import ru.mentor.common.GetAllActiveCoursesPreviewRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.constant.Role;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.CourseTagEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.AdminCourseMapper;
import ru.mentor.mapper.AdminCourseMapperImpl;
import ru.mentor.mapper.AdminModuleMapper;
import ru.mentor.mapper.AdminModuleMapperImpl;
import ru.mentor.mapper.ReactiveBaseMapper;
import ru.mentor.mapper.TagMapper;
import ru.mentor.mapper.TagMapperImpl;
import ru.mentor.mapper.UserMapper;
import ru.mentor.mapper.UserMapperImpl;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.CourseTagLinkRepository;
import ru.mentor.repository.CourseTagRepository;
import ru.mentor.repository.ModuleRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;
import ru.mentor.util.CourseAccessResolver;

@ExtendWith(MockitoExtension.class)
class CourseFacadeImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseTagRepository courseTagRepository;

    @Mock
    private CourseTagLinkRepository courseTagLinkRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private CourseAccessResolver courseAccessResolver;

    @Spy
    private final UserMapper userMapper = new UserMapperImpl();

    @Spy
    private final TagMapper tagMapper = new TagMapperImpl();

    @Spy
    private final ReactiveBaseMapper reactiveBaseMapper = new ReactiveBaseMapper();

    @Spy
    private final AdminModuleMapper moduleMapper = new AdminModuleMapperImpl();

    @Spy
    private AdminCourseMapper courseMapper;

    @Mock
    private CacheAdapter<String, List<CourseResponse>> cache;

    private CourseFacadeImpl courseFacade;

    @BeforeEach
    void setUp() {
        AdminCourseMapper courseMapper = new AdminCourseMapperImpl(moduleMapper, tagMapper, userMapper);

        courseFacade = new CourseFacadeImpl(courseRepository,
                                            userRepository,
                                            courseTagRepository,
                                            moduleRepository,
                                            courseMapper,
                reactiveBaseMapper,
                                            courseTagLinkRepository,
                                            courseAccessResolver,
                                            cache);
    }

    @Test
    public void findCourseWithAuthor_success_returnCourseResponse() {
        CourseEntity courseEntityStub = TestEntityStubGenerator.constructCourseEntity();
        courseEntityStub.setId(TestConstantHolder.COURSE_ID);
        UserEntity userEntityStub = TestEntityStubGenerator.constructAuthorUserEntity();
        userEntityStub.setId(TestConstantHolder.MENTOR_ID);
        CourseResponse expectedCourseResponse = TestGrpcStubGenerator.constructCourseResponse();
        ModuleEntity moduleEntity = TestEntityStubGenerator.constructModuleEntity();
        moduleEntity.setId(TestConstantHolder.MODULE_ID);

        Mockito.when(courseRepository.findByIdOrThrow(TestConstantHolder.COURSE_ID))
                       .thenReturn(Mono.just(courseEntityStub));
        Mockito.when(userRepository.findByIdOrThrow(TestConstantHolder.COURSE_AUTHOR_ID))
                       .thenReturn(Mono.just(userEntityStub));
        Mockito.when(courseTagRepository.findAllByCourseId(TestConstantHolder.COURSE_ID))
                       .thenReturn(Flux.fromIterable(
                               TestEntityStubGenerator.constructCourseTagEntityList(4)));
        Mockito.when(moduleRepository.findAllByCourseId(TestConstantHolder.COURSE_ID))
                       .thenReturn(Flux.just(moduleEntity));

        StepVerifier.create(courseFacade.findCourseById(TestConstantHolder.COURSE_ID))
                .expectNext(expectedCourseResponse)
                .verifyComplete();
    }

    @Test
    public void findAllCourses_success_returnListOfCourseResponse() {
        GrpcPageRequest pageRequestStub =
                TestGrpcStubGenerator.constructGrpcPageRequest();
        UserEntity authorStub = TestEntityStubGenerator.constructAuthorUserEntity();
        authorStub.setId(TestConstantHolder.COURSE_AUTHOR_ID);
        CourseEntity courseEntity = TestEntityStubGenerator.constructCourseEntity();
        courseEntity.setId(TestConstantHolder.COURSE_ID);
        ModuleEntity moduleEntity = TestEntityStubGenerator.constructModuleEntity();
        moduleEntity.setId(TestConstantHolder.MODULE_ID);

        AllCoursesResponse expectedResult =
                TestGrpcStubGenerator.constructAllCoursesResponse();

        Mockito.when(courseAccessResolver.resolveCoursesForUser(authorStub))
               .thenReturn(Flux.just(courseEntity));
        Mockito.when(courseRepository.count())
                       .thenReturn(Mono.just(1L));
        Mockito.when(userRepository.findByIdOrThrow(TestConstantHolder.COURSE_AUTHOR_ID))
                .thenReturn(Mono.just(authorStub));
        Mockito.when(courseTagRepository.findAllByCourseId(TestConstantHolder.COURSE_ID))
               .thenReturn(Flux.fromIterable(
                       TestEntityStubGenerator.constructCourseTagEntityList(4)));
        Mockito.when(moduleRepository.findAllByCourseId(TestConstantHolder.COURSE_ID))
               .thenReturn(Flux.just(moduleEntity));

        StepVerifier.create(courseFacade.findAllCourses(pageRequestStub))
                .expectNext(expectedResult)
                .verifyComplete();
    }

    @Test
    void findAllActiveCoursesPreview_shouldLoadFromDbAndCacheResult() {
        GetAllActiveCoursesPreviewRequest request = Mockito.mock(GetAllActiveCoursesPreviewRequest.class);
        CourseEntity courseEntity = TestEntityStubGenerator.constructCourseEntity();
        courseEntity.setId(TestConstantHolder.COURSE_ID);
        UserEntity authorStub = TestEntityStubGenerator.constructAuthorUserEntity();
        authorStub.setId(TestConstantHolder.COURSE_AUTHOR_ID);
        List<CourseTagEntity> tags = TestEntityStubGenerator.constructCourseTagEntityList(4);
        CourseResponse expectedResponse = TestGrpcStubGenerator.constructCoursePreviewResponse();
        List<CourseResponse> expectedList = List.of(expectedResponse);

        Mockito.when(courseRepository.findAllByIsActiveTrue()).thenReturn(Flux.just(courseEntity));
        Mockito.when(userRepository.findByIdOrThrow(TestConstantHolder.COURSE_AUTHOR_ID))
               .thenReturn(Mono.just(authorStub));
        Mockito.when(courseTagRepository.findAllByCourseId(TestConstantHolder.COURSE_ID))
               .thenReturn(Flux.fromIterable(tags));
        Mockito.when(cache.get(Mockito.eq(TestConstantHolder.ACTIVE_PREVIEWS_CACHE_KEY), Mockito.any()))
               .thenAnswer(invocation -> {
                   Function<String, Mono<List<CourseResponse>>> loader = invocation.getArgument(1);
                   return loader.apply(TestConstantHolder.ACTIVE_PREVIEWS_CACHE_KEY);
               });

        Mono<List<CourseResponse>> result = courseFacade.findAllActiveCoursesPreview(request);

        StepVerifier.create(result)
                    .expectNext(expectedList)
                    .verifyComplete();

        Mockito.verify(cache, Mockito.times(1))
               .get(Mockito.eq(TestConstantHolder.ACTIVE_PREVIEWS_CACHE_KEY), Mockito.any());
        Mockito.verify(courseRepository, Mockito.times(1))
               .findAllByIsActiveTrue();
        Mockito.verify(userRepository, Mockito.times(1))
               .findByIdOrThrow(TestConstantHolder.COURSE_AUTHOR_ID);
        Mockito.verify(courseTagRepository, Mockito.times(1))
               .findAllByCourseId(TestConstantHolder.COURSE_ID);
    }

    @Test
    void findAllActiveCoursesPreview_shouldReturnCachedData() {
        GetAllActiveCoursesPreviewRequest request = Mockito.mock(GetAllActiveCoursesPreviewRequest.class);
        CourseResponse expectedResponse = TestGrpcStubGenerator.constructCourseResponse();
        List<CourseResponse> cachedList = List.of(expectedResponse);

        Mockito.doReturn(Mono.just(cachedList))
               .when(cache).get(Mockito.eq(TestConstantHolder.ACTIVE_PREVIEWS_CACHE_KEY), Mockito.any());

        Mono<List<CourseResponse>> result = courseFacade.findAllActiveCoursesPreview(request);

        StepVerifier.create(result)
                    .expectNext(cachedList)
                    .verifyComplete();

        Mockito.verify(cache, Mockito.times(1))
               .get(Mockito.eq(TestConstantHolder.ACTIVE_PREVIEWS_CACHE_KEY), Mockito.any());
        Mockito.verifyNoInteractions(courseRepository, userRepository, courseTagRepository);
    }

    @Test
    void deleteCourse_shouldInvalidateCache() {
        Long courseId = 1L;
        Mockito.when(courseRepository.deleteById(courseId)).thenReturn(Mono.empty());
        Mockito.when(cache.invalidate(TestConstantHolder.ACTIVE_PREVIEWS_CACHE_KEY))
               .thenReturn(Mono.empty());

        Mono<DeleteCourseResponse> result = courseFacade.deleteCourse(courseId);

        StepVerifier.create(result)
                    .expectNextCount(1)
                    .verifyComplete();

        Mockito.verify(courseRepository, Mockito.times(1))
               .deleteById(courseId);
        Mockito.verify(cache, Mockito.times(1))
               .invalidate(TestConstantHolder.ACTIVE_PREVIEWS_CACHE_KEY);
    }

    @Test
    void createCourse_shouldInvalidateCache() {
        CreateCourseGrpcRequest request = TestGrpcStubGenerator.constructCreateCourseRequest();
        UserEntity author = TestEntityStubGenerator.constructAuthorUserEntity();
        author.setId(TestConstantHolder.COURSE_AUTHOR_ID);
        CourseEntity savedCourse = TestEntityStubGenerator.constructCourseEntity();
        savedCourse.setId(TestConstantHolder.COURSE_ID);
        CourseResponse expectedResponse = TestGrpcStubGenerator.constructCreatedCourseResponse();

        Mockito.when(courseRepository.save(Mockito.any(CourseEntity.class)))
               .thenReturn(Mono.just(savedCourse));
        Mockito.when(courseTagLinkRepository.saveAll(Mockito.anyList())).thenReturn(Flux.empty());
        Mockito.when(cache.invalidate(TestConstantHolder.ACTIVE_PREVIEWS_CACHE_KEY))
               .thenReturn(Mono.empty());

        Mono<CourseResponse> result = courseFacade.createCourse(request, author);

        StepVerifier.create(result)
                    .expectNext(expectedResponse)
                    .verifyComplete();

        Mockito.verify(cache, Mockito.times(1))
               .invalidate(TestConstantHolder.ACTIVE_PREVIEWS_CACHE_KEY);
    }

    @Test
    public void findAllActiveCourses_success_returnListOfOnlyActiveCourses() {
        GrpcPageRequest pageRequestStub = TestGrpcStubGenerator.constructGrpcPageRequest();
        UserEntity user = TestEntityStubGenerator.constructAuthorUserEntity();
        user.setId(TestConstantHolder.COURSE_AUTHOR_ID);
        user.setRole(Role.MENTOR);
        CourseEntity activeCourse = TestEntityStubGenerator.constructCourseEntity();
        activeCourse.setId(1L);
        CourseEntity inactiveCourse = TestEntityStubGenerator.constructCourseEntity();
        inactiveCourse.setId(2L);
        inactiveCourse.setIsActive(false);
        ModuleEntity moduleEntity = TestEntityStubGenerator.constructModuleEntity();
        moduleEntity.setId(TestConstantHolder.MODULE_ID);

        List<CourseEntity> allCourses = Arrays.asList(activeCourse, inactiveCourse);

        Mockito.when(courseAccessResolver.resolveCoursesForUser(user))
               .thenReturn(Flux.fromIterable(allCourses));
        Mockito.when(userRepository.findByIdOrThrow(user.getId()))
               .thenReturn(Mono.just(user));
        Mockito.when(courseTagRepository.findAllByCourseId(TestConstantHolder.COURSE_ID))
               .thenReturn(Flux.fromIterable(
                       TestEntityStubGenerator.constructCourseTagEntityList(4)));
        Mockito.when(moduleRepository.findAllByCourseId(TestConstantHolder.COURSE_ID))
               .thenReturn(Flux.just(moduleEntity));

        Mockito.when(courseRepository.count()).thenReturn(Mono.just(1L));

        AllCoursesResponse expected = TestGrpcStubGenerator.constructAllCoursesResponse();

        StepVerifier.create(courseFacade.findAllActiveCourses(pageRequestStub))
                    .expectNext(expected)
                    .verifyComplete();

        Mockito.verify(moduleRepository, Mockito.never()).findAllByCourseId(inactiveCourse.getId());
        Mockito.verify(courseTagRepository, Mockito.never()).findAllByCourseId(inactiveCourse.getId());
    }
}