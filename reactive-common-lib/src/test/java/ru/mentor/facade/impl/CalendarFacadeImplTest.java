package ru.mentor.facade.impl;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.mapper.UserMapper;
import ru.mentor.repository.MentorTimeSlotRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class CalendarFacadeImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorTimeSlotRepository timeSlotRepository;

    @Spy
    private TimeSlotMapper timeSlotMapper = new TimeSlotMapper();

    @Spy
    private final BaseMapper baseMapper = new BaseMapper();

    @Spy
    private final UserMapper userMapper = new UserMapper();

    @InjectMocks
    private CalendarFacadeImpl calendarFacade;

    private GrpcPageRequest grpcPageRequestStub;
    private PageRequest pageRequestStub;


    @BeforeEach
    void setUp() {
        grpcPageRequestStub =
                TestGrpcStubGenerator.constructGrpcPageRequest();
        pageRequestStub =
                baseMapper.mapGrpcPageRequestToPageRequest(grpcPageRequestStub);
    }

    @Test
    void findAllTimeSlotsResponseByGrpcPageRequest_success_returnAllTimeSlotsResponse() {
        MentorTimeSlotEntity mentorTimeSlotEntityStub =
                TestEntityStubGenerator.constructMentorTimeSlotEntity();
        UserEntity userEntityStub = TestEntityStubGenerator.constructParticipantEntity();

        AllTimeSlotsResponse expectedAllTimeSlotsResponse =
                TestGrpcStubGenerator.constructAllTimeSlotsResponse();

        Mockito.when(userRepository.findAllSlotParticipantsBySlotId(TestConstantHolder.SLOT_ID))
               .thenReturn(Flux.just(userEntityStub));
        Mockito.when(timeSlotRepository.findAllBy(pageRequestStub))
               .thenReturn(Flux.just(mentorTimeSlotEntityStub));
        Mockito.when(timeSlotRepository.count())
               .thenReturn(Mono.just(1L));

        StepVerifier.create(calendarFacade.findAllTimeSlotsResponseByGrpcPageRequest(
                            grpcPageRequestStub))
                    .expectNext(expectedAllTimeSlotsResponse)
                    .verifyComplete();
    }

    @Test
    void findAllTimeSlotsResponseByGrpcPageRequest_noSlotsFound_returnsEmptyResult() {

        Mockito.when(timeSlotRepository.findAllBy(pageRequestStub))
               .thenReturn(Flux.empty());
        Mockito.when(timeSlotRepository.count())
               .thenReturn(Mono.just(0L));

        StepVerifier.create(calendarFacade.findAllTimeSlotsResponseByGrpcPageRequest(
                            grpcPageRequestStub))
                    .expectNextMatches(response -> response.getTimeSlotsList().isEmpty() &&
                            response.getPageDetails().getTotalElements() == 0L &&
                            response.getPageDetails().getTotalPages() == 0)
                    .verifyComplete();
    }

    @Test
    void findAllTimeSlotsResponseByGrpcPageRequest_multipleSlotsFound_returnsAllTimeSlotsResponse() {
        List<MentorTimeSlotEntity> timeSlotEntityList =
                TestEntityStubGenerator.constructMentorTimeSlotEntityList();
        GrpcPageRequest grpcPageRequestStub =
                TestGrpcStubGenerator.constructGrpcPageRequest();
        PageRequest pageRequestStub =
                baseMapper.mapGrpcPageRequestToPageRequest(grpcPageRequestStub);

        Mockito.when(timeSlotRepository.findAllBy(pageRequestStub))
               .thenReturn(Flux.fromIterable(timeSlotEntityList));
        Mockito.when(timeSlotRepository.count())
               .thenReturn(Mono.just((long) timeSlotEntityList.size()));
        Mockito.when(userRepository.findAllSlotParticipantsBySlotId(TestConstantHolder.SLOT_ID))
               .thenReturn(Flux.just(TestEntityStubGenerator.constructParticipantEntity()));
        Mockito.when(userRepository.findAllSlotParticipantsBySlotId(TestConstantHolder.SLOT_ID + 1))
               .thenReturn(Flux.empty());

        StepVerifier.create(calendarFacade.findAllTimeSlotsResponseByGrpcPageRequest(
                            grpcPageRequestStub))
                    .expectNextMatches(response ->
                                               response.getTimeSlotsList().size()
                                                       == timeSlotEntityList.size())
                    .verifyComplete();
    }

}