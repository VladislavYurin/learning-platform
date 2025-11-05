package ru.mentor.facade.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.AdminCourseMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.TagMapper;
import ru.mentor.mapper.UserMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.CourseTagRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class CourseFacadeImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseTagRepository courseTagRepository;

    @Spy
    private final UserMapper userMapper = new UserMapper();

    @Spy
    private final TagMapper tagMapper = new TagMapper();

    @Spy
    private final BaseMapper baseMapper = new BaseMapper();

    private CourseFacadeImpl courseFacade;

    @BeforeEach
    void setUp() {
        AdminCourseMapper courseMapper = new AdminCourseMapper(userMapper, tagMapper);

        courseFacade = new CourseFacadeImpl(courseRepository,
                                            userRepository,
                                            courseTagRepository,
                                            courseMapper,
                                            baseMapper);
    }

    @Test
    public void findCourseWithAuthor_success_returnCourseResponse() {
        CourseEntity courseEntityStub = TestEntityStubGenerator.constructCourseEntity();
        UserEntity userEntityStub = TestEntityStubGenerator.constructAuthorUserEntity();
        CourseResponse expectedCourseResponse = TestGrpcStubGenerator.constructCourseResponse();

        Mockito.when(courseRepository.findByIdOrThrow(TestConstantHolder.COURSE_ID))
                       .thenReturn(Mono.just(courseEntityStub));
        Mockito.when(userRepository.findByIdOrThrow(TestConstantHolder.COURSE_AUTHOR_ID))
                       .thenReturn(Mono.just(userEntityStub));
        Mockito.when(courseTagRepository.findAllByCourseId(TestConstantHolder.COURSE_ID))
                       .thenReturn(Flux.fromIterable(
                               TestEntityStubGenerator.constructCourseTagEntityList(4)));

        StepVerifier.create(courseFacade.findCourseById(TestConstantHolder.COURSE_ID))
                .expectNext(expectedCourseResponse)
                .verifyComplete();
    }

    @Test
    public void findAllCourses_success_returnListOfCourseResponse() {
        GrpcPageRequest pageRequestStub =
                TestGrpcStubGenerator.constructGrpcPageRequest();
        PageRequest pageRequest =
                baseMapper.mapGrpcPageRequestToPageRequest(pageRequestStub);
        UserEntity authorStub = TestEntityStubGenerator.constructAuthorUserEntity();

        AllCoursesResponse expectedResult =
                TestGrpcStubGenerator.constructAllCoursesResponse();


        Mockito.when(courseRepository.findAllBy(pageRequest))
                .thenReturn(Flux.just(TestEntityStubGenerator.constructCourseEntity()));
        Mockito.when(courseRepository.count())
                       .thenReturn(Mono.just(1L));
        Mockito.when(userRepository.findByIdOrThrow(TestConstantHolder.COURSE_AUTHOR_ID))
                .thenReturn(Mono.just(authorStub));
        Mockito.when(courseTagRepository.findAllByCourseId(TestConstantHolder.COURSE_ID))
               .thenReturn(Flux.fromIterable(
                       TestEntityStubGenerator.constructCourseTagEntityList(4)));

        StepVerifier.create(courseFacade.findAllCourses(pageRequestStub))
                .expectNext(expectedResult)
                .verifyComplete();
    }
}