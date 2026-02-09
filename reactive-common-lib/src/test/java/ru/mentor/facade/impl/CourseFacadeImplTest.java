package ru.mentor.facade.impl;

import java.util.Arrays;
import java.util.List;
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
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.constant.Role;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.AdminCourseMapper;
import ru.mentor.mapper.AdminModuleMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.TagMapper;
import ru.mentor.mapper.UserMapper;
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
    private final UserMapper userMapper = new UserMapper();

    @Spy
    private final TagMapper tagMapper = new TagMapper();

    @Spy
    private final BaseMapper baseMapper = new BaseMapper();

    @Spy
    private final AdminModuleMapper moduleMapper = new AdminModuleMapper();

    private CourseFacadeImpl courseFacade;

    @BeforeEach
    void setUp() {
        AdminCourseMapper courseMapper = new AdminCourseMapper(userMapper, tagMapper, moduleMapper);

        courseFacade = new CourseFacadeImpl(courseRepository,
                                            userRepository,
                                            courseTagRepository,
                                            moduleRepository,
                                            courseMapper,
                                            baseMapper,
                                            courseTagLinkRepository,
                                            courseAccessResolver);
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