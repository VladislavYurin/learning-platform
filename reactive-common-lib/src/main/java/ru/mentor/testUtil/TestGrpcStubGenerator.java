package ru.mentor.testUtil;

import com.google.protobuf.Timestamp;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.mentor.admin.AllCoursesResponse;
import ru.mentor.admin.AllModulesResponse;
import ru.mentor.admin.AllTimeSlotsResponse;
import ru.mentor.admin.AuthorResponse;
import ru.mentor.admin.CourseResponse;
import ru.mentor.admin.GetAllModulesRequest;
import ru.mentor.admin.GetCourseRequest;
import ru.mentor.admin.GetModuleRequest;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.admin.ModuleResponse;
import ru.mentor.admin.PageDetails;
import ru.mentor.calendar.MentorSlotInfo;
import ru.mentor.calendar.Role;
import ru.mentor.calendar.TimeSlotResponse;
import ru.mentor.calendar.UserInfo;

public final class TestGrpcStubGenerator {

    private TestGrpcStubGenerator() {
    }

    public static GrpcPageRequest constructGrpcPageRequest() {
        return GrpcPageRequest.newBuilder()
                              .setRequestId(TestConstantHolder.REQUEST_ID)
                              .setPageNumber(TestConstantHolder.PAGE_NUMBER)
                              .setPageSize(TestConstantHolder.PAGE_SIZE)
                              .build();
    }

    public static PageDetails constructPageDetails() {
        return PageDetails.newBuilder()
                          .setPage(TestConstantHolder.PAGE_NUMBER)
                          .setSize(TestConstantHolder.PAGE_SIZE)
                          .setTotalElements(TestConstantHolder.TOTAL_ELEMENTS_COUNT)
                          .setTotalPages(TestConstantHolder.TOTAL_PAGES_COUNT)
                          .build();
    }

    public static TimeSlotResponse constructTimeSlotResponse() {
        return TimeSlotResponse.newBuilder()
                               .setRqUid(TestConstantHolder.REQUEST_ID)
                               .setSlotId(TestConstantHolder.SLOT_ID)
                               .setMentorId(TestConstantHolder.MENTOR_ID)
                               .setStartTime(TestConstantHolder.SLOT_START_TIMESTAMP)
                               .setEndTime(TestConstantHolder.SLOT_END_TIMESTAMP)
                               .setSlotType(TestConstantHolder.GRPC_SLOT_TYPE)
                               .setSlotMeetingType(TestConstantHolder.GRPC_SLOT_MEETING_TYPE)
                               .setMaxParticipants(TestConstantHolder.MAX_PARTICIPANTS)
                               .setMeetingLink(TestConstantHolder.MEETING_LINK)
                               .setDescription(TestConstantHolder.SLOT_DESCRIPTION)
                               .setCreatedAt(TestConstantHolder.SLOT_CREATED_AT_TIMESTAMP)
                               .build();
    }

    public static UserInfo constructUserInfo(long userId) {
        return UserInfo.newBuilder()
                       .setId(userId)
                       .setUsername(TestConstantHolder.USERNAME)
                       .setRole(Role.USER)
                       .setFirstName(TestConstantHolder.FIRST_NAME)
                       .setLastName(TestConstantHolder.LAST_NAME)
                       .setTgNickname(TestConstantHolder.TG_NICKNAME)
                       .setTgChatId(TestConstantHolder.TG_CHAT_ID)
                       .build();
    }

    public static MentorSlotInfo constructMentorSlotInfo() {
        return MentorSlotInfo.newBuilder()
                             .setSlotInfo(constructTimeSlotResponse())
                             .addAllParticipants(constructMentorSlotParticipants())
                             .build();
    }

    public static AllTimeSlotsResponse constructAllTimeSlotsResponse() {
        return AllTimeSlotsResponse.newBuilder()
                                   .setPageDetails(constructPageDetails())
                                   .addAllTimeSlots(List.of(constructMentorSlotInfo()))
                                   .build();
    }

    public static CourseResponse constructCourseResponse() {
        return CourseResponse.newBuilder()
                             .setCourseId(TestConstantHolder.COURSE_ID)
                             .setTitle(TestConstantHolder.COURSE_TITLE)
                             .setDescription(TestConstantHolder.COURSE_DESCRIPTION)
                             .setIsActive(TestConstantHolder.IS_ACTIVE_COURSE)
                             .setCreatedAt(Timestamp.newBuilder()
                                                    .setSeconds(TestConstantHolder.CREATED_AT_EPOCH_SECONDS)
                                                    .build())
                             .setAuthor(constructCourseAuthorResponse())
                             .build();
    }

    public static AuthorResponse constructCourseAuthorResponse() {
        return AuthorResponse.newBuilder()
                             .setUserId(TestConstantHolder.MENTOR_ID)
                             .setUsername(TestConstantHolder.USERNAME)
                             .setFirstName(TestConstantHolder.FIRST_NAME)
                             .setLastName(TestConstantHolder.LAST_NAME)
                             .setTgNickname(TestConstantHolder.TG_NICKNAME)
                             .setTgChatId(TestConstantHolder.TG_CHAT_ID)
                             .build();
    }

    public static GetCourseRequest constructGetCourseRequest() {
        return GetCourseRequest.newBuilder()
                               .setRequestId(TestConstantHolder.REQUEST_ID)
                               .setCourseId(TestConstantHolder.COURSE_ID)
                               .build();
    }

    public static AllCoursesResponse constructAllCoursesResponse() {
        return AllCoursesResponse.newBuilder()
                                 .setPageDetails(constructPageDetails())
                                 .addAllCourses(List.of(constructCourseResponse()))
                                 .build();
    }

    public static GetModuleRequest constructGetModuleRequest() {
        return GetModuleRequest.newBuilder()
                               .setRequestId(TestConstantHolder.REQUEST_ID)
                               .setModuleId(TestConstantHolder.MODULE_ID)
                               .build();
    }

    public static ModuleResponse constructModuleResponse() {
        return ModuleResponse.newBuilder()
                             .setModuleId(TestConstantHolder.MODULE_ID)
                             .setTitle(TestConstantHolder.MODULE_TITLE)
                             .setOrderNumber(TestConstantHolder.MODULE_ORDER_NUMBER)
                             .setContent(TestConstantHolder.MODULE_CONTENT)
                             .setIsActive(TestConstantHolder.IS_ACTIVE_MODULE)
                             .setCreatedAt(TestConstantHolder.MODULE_CREATED_AT_TIMESTAMP)
                             .setCourseId(TestConstantHolder.COURSE_ID)
                             .build();
    }

    public static GetAllModulesRequest constructGetAllModulesRequest() {
        return GetAllModulesRequest.newBuilder()
                                   .setRequestId(TestConstantHolder.REQUEST_ID)
                                   .setCourseId(TestConstantHolder.COURSE_ID)
                                   .build();
    }

    public static AllModulesResponse constructAllModulesResponse() {
        return AllModulesResponse.newBuilder()
                                 .setPageDetails(PageDetails.newBuilder()
                                                            .setPage(TestConstantHolder.PAGE_NUMBER)
                                                            .setSize(TestConstantHolder.PAGE_SIZE)
                                                            .setTotalElements(TestConstantHolder.TOTAL_ELEMENTS_COUNT)
                                                            .setTotalPages(TestConstantHolder.TOTAL_PAGES_COUNT)
                                                            .build())
                                 .addAllModules(List.of(constructModuleResponse()))
                                 .build();
    }

    public static List<UserInfo> constructMentorSlotParticipants() {
        return List.of(constructUserInfo(TestConstantHolder.USER_ID));
    }

    public static Page<CourseResponse> constructCourseResponsePage() {
        return new PageImpl<>(
                List.of(constructCourseResponse()),
                PageRequest.of(TestConstantHolder.PAGE_NUMBER, TestConstantHolder.PAGE_SIZE),
                TestConstantHolder.TOTAL_ELEMENTS_COUNT
        );
    }

    public static Page<ModuleResponse> constructModuleResponsePage() {
        return new PageImpl<>(
                List.of(constructModuleResponse()),
                PageRequest.of(TestConstantHolder.PAGE_NUMBER, TestConstantHolder.PAGE_SIZE),
                TestConstantHolder.TOTAL_ELEMENTS_COUNT
        );
    }

    public static Page<MentorSlotInfo> constructMentorSlotInfoPage() {
        return new PageImpl<>(
                List.of(constructMentorSlotInfo()),
                PageRequest.of(TestConstantHolder.PAGE_NUMBER, TestConstantHolder.PAGE_SIZE),
                TestConstantHolder.TOTAL_ELEMENTS_COUNT
        );
    }

}

