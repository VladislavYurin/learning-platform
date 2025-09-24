package ru.mentor.testUtil;

import com.google.protobuf.Timestamp;
import java.time.ZoneOffset;
import java.util.List;
import ru.mentor.admin.AllCoursesResponse;
import ru.mentor.admin.AllModulesResponse;
import ru.mentor.admin.AllTimeSlotsResponse;
import ru.mentor.admin.AuthorResponse;
import ru.mentor.admin.CourseResponse;
import ru.mentor.admin.GetCourseRequest;
import ru.mentor.admin.GetModuleRequest;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.admin.ModuleResponse;
import ru.mentor.admin.PageDetails;
import ru.mentor.calendar.MentorSlotInfo;
import ru.mentor.calendar.Role;
import ru.mentor.calendar.TimeSlotResponse;
import ru.mentor.calendar.UserInfo;

public class TestGrpcStubGenerator {

    public static GetModuleRequest constructGetModuleRequest() {
        return GetModuleRequest.newBuilder()
                               .setRequestId(TestConstantHolder.requestId)
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
                              .setRequestId(TestConstantHolder.requestId)
                              .setPageNumber(TestConstantHolder.pageNumber)
                              .setPageSize(TestConstantHolder.pageSize)
                              .build();
    }

    public static GetCourseRequest constructGetCourseRequest() {
        return GetCourseRequest.newBuilder()
                               .setRequestId(TestConstantHolder.requestId)
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
                               .setRqUid(TestConstantHolder.requestId)
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

}
