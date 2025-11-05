package ru.mentor.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.dto.MentorSlotInfoDto;
import ru.mentor.grpc.AdminCalendarServiceGrpcClient;
import ru.mentor.grpc.HeaderFactory;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.services.impl.RedirectAdminCalendarServiceImpl;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class RedirectAdminCalendarServiceImplTest {

    @Mock
    private HeaderFactory headerFactory;
    @Mock
    private UserService userService;
    @Mock
    private AdminCalendarServiceGrpcClient calendarServiceGrpcClient;

    private TimeSlotMapper timeSlotMapper;
    private BaseMapper baseMapper;
    private RedirectAdminCalendarServiceImpl redirectService;

    @BeforeEach
    void setUp() {
        Mockito.lenient()
                .when(headerFactory.create(Mockito.anyString()))
                .thenAnswer(inv -> ru.mentor.common.Header.newBuilder()
                        .setRequestId(inv.getArgument(0, String.class))
                        .setNodeId("test-node")
                        .setApiKey("test-api")
                        .build());

        timeSlotMapper = Mockito.spy(new TimeSlotMapper(headerFactory));
        baseMapper = Mockito.spy(new BaseMapper(headerFactory)
        );

        redirectService = new RedirectAdminCalendarServiceImpl(
                userService,
                calendarServiceGrpcClient,
                timeSlotMapper,
                baseMapper
        );
    }

    @Test
    void getAllMentorTimeSlots_success() {

        AllTimeSlotsResponse grpcResponse = TestGrpcStubGenerator.constructAllTimeSlotsResponse();
        Page<MentorSlotInfoDto> expectedResult = TestEntityStubGenerator.constructMentoSlotInfoDtoPage();

        Mockito.when(userService.getCurrentUserId()).thenReturn(TestConstantHolder.userId);
        Mockito.when(calendarServiceGrpcClient.getAllTimeSlots(Mockito.any(GrpcPageRequest.class)))
               .thenReturn(grpcResponse);

        Page<MentorSlotInfoDto> result = redirectService.getAllMentorTimeSlots(
                TestConstantHolder.pageNumber,
                TestConstantHolder.pageSize
        );

        Assertions.assertThat(result.getContent())
                  .isEqualTo(expectedResult.getContent());

        Assertions.assertThat(result.getTotalElements())
                  .isEqualTo(expectedResult.getTotalElements());

        Assertions.assertThat(result.getTotalPages())
                  .isEqualTo(expectedResult.getTotalPages());

        Assertions.assertThat(result.getSize())
                  .isEqualTo(expectedResult.getSize());
    }

    @Test
    void getAllMentorTimeSlots_failure() {

        GrpcPageRequest grpcPageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();

        Mockito.when(userService.getCurrentUserId()).thenReturn(TestConstantHolder.userId);
        Mockito.when(baseMapper.constructGrpcPageRequest(
                       ArgumentMatchers.anyString(),
                       ArgumentMatchers.anyInt(),
                       ArgumentMatchers.anyInt()
               ))
               .thenReturn(grpcPageRequest);
        Mockito.when(calendarServiceGrpcClient.getAllTimeSlots(grpcPageRequest))
               .thenThrow(new RuntimeException(TestConstantHolder.notFoundExceptionText));

        Assertions.assertThatThrownBy(() -> redirectService.getAllMentorTimeSlots(
                          TestConstantHolder.pageNumber, TestConstantHolder.pageSize
                  ))
                  .isInstanceOf(RuntimeException.class)
                  .hasMessageContaining(TestConstantHolder.notFoundExceptionText);

        Mockito.verify(userService).getCurrentUserId();
        Mockito.verify(baseMapper).constructGrpcPageRequest(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
        );
        Mockito.verify(calendarServiceGrpcClient).getAllTimeSlots(grpcPageRequest);
    }

}