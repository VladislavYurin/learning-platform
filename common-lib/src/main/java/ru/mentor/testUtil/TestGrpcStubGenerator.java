package ru.mentor.testUtil;

import com.google.protobuf.Timestamp;
import java.time.ZoneOffset;
import java.util.List;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.AuthorResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.GetAllModulesRequest;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.Header;
import ru.mentor.common.MentorSlotInfo;
import ru.mentor.common.MentorSlotsInfoResponse;
import ru.mentor.common.ModuleResponse;
import ru.mentor.common.PageDetails;
import ru.mentor.common.Role;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.common.UserInfo;

public class TestGrpcStubGenerator {

    private static Header buildTestHeader() {
        return Header.newBuilder()
                     .setRequestId(TestConstantHolder.requestId)
                     .setNodeId(TestConstantHolder.nodeId)
                     .setApiKey(TestConstantHolder.apiKey)
                     .build();
    }

    public static GetModuleRequest constructGetModuleRequest() {
        return GetModuleRequest.newBuilder()
                               .setHeader(buildTestHeader())
                               .setModuleId(TestConstantHolder.moduleId)
                               .build();
    }

    public static AllModulesResponse constructAllModulesResponse() {
        return AllModulesResponse.newBuilder()
                                 .setPageDetails(constructPageDetails())
                                 .addAllModules(List.of(constructModuleResponse()))
                                 .build();
    }

    public static GrpcPageRequest constructGrpcPageRequest() {
        return GrpcPageRequest.newBuilder()
                              .setHeader(buildTestHeader())
                              .setPageNumber(TestConstantHolder.pageNumber)
                              .setPageSize(TestConstantHolder.pageSize)
                              .build();
    }

    public static GetAllModulesRequest constructGetAllModulesRequest() {
        return GetAllModulesRequest.newBuilder()
                                   .setHeader(buildTestHeader())
                                   .setCourseId(TestConstantHolder.courseId)
                                   .build();
    }

    public static GetCourseRequest constructGetCourseRequest() {
        return GetCourseRequest.newBuilder()
                               .setHeader(buildTestHeader())
                               .setCourseId(TestConstantHolder.courseId)
                               .build();
    }

    public static PageDetails constructPageDetails() {
        return PageDetails.newBuilder()
                          .setPage(TestConstantHolder.pageNumber)
                          .setSize(TestConstantHolder.pageSize)
                          .setTotalElements(TestConstantHolder.totalElementsCount)
                          .setTotalPages(TestConstantHolder.totalPagesCount)
                          .build();
    }

    public static ModuleResponse constructModuleResponse() {
        return ModuleResponse.newBuilder()
                             .setModuleId(TestConstantHolder.moduleId)
                             .setTitle(TestConstantHolder.moduleTitle)
                             .setOrderNumber(TestConstantHolder.moduleOrderNumber)
                             .setContent(TestConstantHolder.moduleContent)
                             .setIsActive(TestConstantHolder.isActive)
                             .setCreatedAt(constructCreatedAtTimestamp())
                             .setCourseId(TestConstantHolder.courseId)
                             .build();
    }

    public static AuthorResponse constructAuthorResponse() {
        return AuthorResponse.newBuilder()
                             .setUserId(TestConstantHolder.userId)
                             .setUsername(TestConstantHolder.username)
                             .setFirstName(TestConstantHolder.firstName)
                             .setLastName(TestConstantHolder.lastName)
                             .setTgNickname(TestConstantHolder.tgNickname)
                             .setTgChatId(TestConstantHolder.tgChatId)
                             .build();
    }

    public static AllCoursesResponse constructAllCoursesResponse() {
        return AllCoursesResponse.newBuilder()
                                 .setPageDetails(constructPageDetails())
                                 .addCourses(constructCourseResponse())
                                 .build();
    }

    public static CourseResponse constructCourseResponse() {
        return CourseResponse.newBuilder()
                             .setCourseId(TestConstantHolder.courseId)
                             .setTitle(TestConstantHolder.courseTitle)
                             .setDescription(TestConstantHolder.courseDescription)
                             .setIsActive(TestConstantHolder.isActive)
                             .setCreatedAt(TestConstantHolder.slotCreatedAtTimestamp)
                             .setAuthor(constructAuthorResponse())
                             .build();
    }

    public static Timestamp constructCreatedAtTimestamp() {
        return Timestamp.newBuilder()
                        .setSeconds(TestConstantHolder.createdAt.toEpochSecond(ZoneOffset.UTC))
                        .build();
    }

    public static TimeSlotResponse constructTimeSlotResponse() {
        return TimeSlotResponse.newBuilder()
                               .setRequestId(TestConstantHolder.requestId)
                               .setSlotId(TestConstantHolder.timeSlotId)
                               .setMentorId(TestConstantHolder.mentorId)
                               .setStartTime(TestConstantHolder.slotStartTimestamp)
                               .setEndTime(TestConstantHolder.slotEndTimestamp)
                               .setSlotType(TestConstantHolder.grpcSlotType)
                               .setSlotMeetingType(TestConstantHolder.grpcSlotMeetingType)
                               .setMaxParticipants(TestConstantHolder.maxParticipants)
                               .setMeetingLink(TestConstantHolder.meetingLink)
                               .setDescription(TestConstantHolder.slotDescription)
                               .setCreatedAt(TestConstantHolder.slotCreatedAtTimestamp)
                               .build();
    }

    public static UserInfo constructUserInfo(long userId) {
        return UserInfo.newBuilder()
                       .setId(userId)
                       .setUsername(TestConstantHolder.username)
                       .setRole(Role.USER)
                       .setFirstName(TestConstantHolder.firstName)
                       .setLastName(TestConstantHolder.lastName)
                       .setTgNickname(TestConstantHolder.tgNickname)
                       .setTgChatId(TestConstantHolder.tgChatId)
                       .build();
    }

    public static MentorSlotInfo constructMentorSlotInfo() {
        return MentorSlotInfo.newBuilder()
                             .setSlotInfo(constructTimeSlotResponse())
                             .addAllParticipants(
                                     List.of(
                                             constructUserInfo(TestConstantHolder.userId)))
                             .build();
    }

    public static AllTimeSlotsResponse constructAllTimeSlotsResponse() {
        return AllTimeSlotsResponse.newBuilder()
                                   .setPageDetails(constructPageDetails())
                                   .addAllTimeSlots(List.of(constructMentorSlotInfo()))
                                   .build();
    }

    public static MentorSlotsInfoResponse constructMentorSlotsInfoResponse() {
        return MentorSlotsInfoResponse.newBuilder()
                                      .addAllSlots(List.of(constructMentorSlotInfo()))
                                      .build();
    }

}
