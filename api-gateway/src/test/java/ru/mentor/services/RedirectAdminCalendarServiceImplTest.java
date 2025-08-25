package ru.mentor.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.mentor.admin.AllTimeSlotsResponse;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.admin.PageDetails;
import ru.mentor.dto.MentorSlotInfoDto;
import ru.mentor.dto.PageSettings;
import ru.mentor.grpc.AdminCalendarServiceGrpcClient;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.services.impl.RedirectAdminCalendarServiceImpl;

@ExtendWith(MockitoExtension.class)
class RedirectAdminCalendarServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private AdminCalendarServiceGrpcClient calendarServiceGrpcClient;
    @Mock
    private TimeSlotMapper timeSlotMapper;
    @Mock
    private BaseMapper baseMapper;

    @InjectMocks
    private RedirectAdminCalendarServiceImpl redirectService;

    private final String requestId = UUID.randomUUID().toString();
    private final int pageNumber = 0;
    private final int pageSize = 10;
    private final long currentUserId = 1L;
    private final String slotNotFoundExceptionText = "Slots not found";

    @Test
    void getAllMentorTimeSlots_success() {
        PageSettings pageSettings = new PageSettings(pageNumber, pageSize);

        GrpcPageRequest grpcPageRequest = constructGrpcPageRequest();
        AllTimeSlotsResponse grpcResponse = constructAllTimeSlotsResponse();

        List<MentorSlotInfoDto> dtoList = List.of(
                Mockito.mock(MentorSlotInfoDto.class));
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Mockito.when(userService.getCurrentUserId()).thenReturn(currentUserId);
        Mockito.when(baseMapper.constructGrpcPageRequest(anyString(), eq(pageSettings)))
               .thenReturn(grpcPageRequest);
        Mockito.when(calendarServiceGrpcClient.getAllTimeSlots(grpcPageRequest))
               .thenReturn(grpcResponse);
        Mockito.when(timeSlotMapper.mapGrpcAllTimeSlotsResponseToMentorSlotInfoDtoList(grpcResponse))
               .thenReturn(dtoList);
        Mockito.when(baseMapper.mapGrpcPageDetailsToPageRequest(grpcResponse.getPageDetails()))
               .thenReturn(pageRequest);

        Page<MentorSlotInfoDto> result = redirectService.getAllMentorTimeSlots(pageSettings);

        assertThat(result.getContent()).isEqualTo(dtoList);
        assertThat(result.getTotalElements())
                .isEqualTo(grpcResponse.getPageDetails().getTotalElements());
    }

    @Test
    void getAllMentorTimeSlots_failure() {
        PageSettings pageSettings = new PageSettings(pageNumber, pageSize);

        GrpcPageRequest grpcPageRequest = constructGrpcPageRequest();

        Mockito.when(userService.getCurrentUserId()).thenReturn(currentUserId);
        Mockito.when(baseMapper.constructGrpcPageRequest(anyString(), eq(pageSettings)))
               .thenReturn(grpcPageRequest);
        Mockito.when(calendarServiceGrpcClient.getAllTimeSlots(grpcPageRequest))
               .thenThrow(new RuntimeException(slotNotFoundExceptionText));

        assertThatThrownBy(() -> redirectService.getAllMentorTimeSlots(pageSettings))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(slotNotFoundExceptionText);

        Mockito.verify(userService).getCurrentUserId();
        Mockito.verify(baseMapper).constructGrpcPageRequest(anyString(), eq(pageSettings));
        Mockito.verify(calendarServiceGrpcClient).getAllTimeSlots(grpcPageRequest);
    }

    private GrpcPageRequest constructGrpcPageRequest() {
        return GrpcPageRequest.newBuilder()
                              .setRequestId(requestId)
                              .build();
    }

    private PageDetails constructPageDetails() {
        return PageDetails.newBuilder()
                          .setPage(
                                  pageNumber)
                          .setSize(
                                  pageSize)
                          .setTotalElements(1)
                          .setTotalPages(1)
                          .build();
    }

    private AllTimeSlotsResponse constructAllTimeSlotsResponse() {
        return AllTimeSlotsResponse.newBuilder()
                                   .setPageDetails(constructPageDetails())
                                   .build();
    }

}