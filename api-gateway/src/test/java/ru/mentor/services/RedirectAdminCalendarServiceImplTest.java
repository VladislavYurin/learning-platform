package ru.mentor.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.dto.MentorSlotInfoDto;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.AdminCalendarServiceGrpcClient;
import ru.mentor.mapper.BaseMapperImpl;
import ru.mentor.mapper.TimeSlotMapperImpl;
import ru.mentor.mapper.UtilMapperImpl;
import ru.mentor.services.impl.RedirectAdminCalendarServiceImpl;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@SpringBootTest(classes = {
        RedirectAdminCalendarServiceImpl.class,
        BaseMapperImpl.class,
        TimeSlotMapperImpl.class,
        UtilMapperImpl.class
})
class RedirectAdminCalendarServiceImplTest {

    @MockBean
    private AdminCalendarServiceGrpcClient calendarServiceGrpcClient;

    @MockBean
    private UserService userService;

    @MockBean
    private HeaderFactory headerFactory;

    @Autowired
    private RedirectAdminCalendarServiceImpl redirectService;

    @BeforeEach
    void setUp() {
        Mockito.when(headerFactory.create(ArgumentMatchers.anyString()))
                .thenReturn(TestGrpcStubGenerator.constructHeader());
    }

    @Test
    void getAllMentorTimeSlots_success() {

        AllTimeSlotsResponse grpcResponse = TestGrpcStubGenerator.constructAllTimeSlotsResponse();
        Page<MentorSlotInfoDto> expectedResult = TestEntityStubGenerator.constructMentorSlotInfoDtoPage();

        Mockito.when(userService.getCurrentUserId()).thenReturn(TestConstantHolder.userId);
        Mockito.when(calendarServiceGrpcClient.getAllTimeSlots(Mockito.any(GrpcPageRequest.class)))
                .thenReturn(grpcResponse);

        Page<MentorSlotInfoDto> result = redirectService.getAllMentorTimeSlots(
                TestConstantHolder.zero,
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

        Mockito.when(userService.getCurrentUserId())
                .thenReturn(TestConstantHolder.userId);

        Mockito.when(calendarServiceGrpcClient.getAllTimeSlots(ArgumentMatchers.any(GrpcPageRequest.class)))
                .thenThrow(new RuntimeException(TestConstantHolder.notFoundExceptionText));

        Assertions.assertThatThrownBy(() -> redirectService.getAllMentorTimeSlots(
                        TestConstantHolder.zero,
                        TestConstantHolder.pageSize
                ))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(TestConstantHolder.notFoundExceptionText);

        Mockito.verify(userService).getCurrentUserId();
        Mockito.verify(calendarServiceGrpcClient).getAllTimeSlots(ArgumentMatchers.any(GrpcPageRequest.class));
    }

}