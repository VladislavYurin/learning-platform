package ru.mentor.facade.impl;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.TimeSlotMapperImpl;
import ru.mentor.mapper.UserMapperImpl;
import ru.mentor.mapper.UtilMapperImpl;
import ru.mentor.repository.MentorTimeSlotRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@SpringBootTest(classes = {
        CalendarFacadeImpl.class,
        BaseMapper.class,
        TimeSlotMapperImpl.class,
        UserMapperImpl.class,
        UtilMapperImpl.class})
class CalendarFacadeImplTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MentorTimeSlotRepository timeSlotRepository;

    @Autowired
    private BaseMapper baseMapper = new BaseMapper();

    @Autowired
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