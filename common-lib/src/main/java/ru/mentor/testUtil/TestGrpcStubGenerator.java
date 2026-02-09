package ru.mentor.testUtil;

import com.google.protobuf.ByteString;
import java.util.List;

import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.AuthorResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.CreateCourseGrpcRequest;
import ru.mentor.common.CreateCourseTagGrpcRequest;
import ru.mentor.common.CreateModuleGrpcRequest;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.common.DeleteCourseRequest;
import ru.mentor.common.DeleteCourseTagRequest;
import ru.mentor.common.DeleteModuleRequest;
import ru.mentor.common.GetAllCourseTagsRequest;
import ru.mentor.common.GetAllModulesRequest;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GetCourseTagRequest;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.Header;
import ru.mentor.common.ImportModuleFromFileRequest;
import ru.mentor.common.ListCourseTagsResponse;
import ru.mentor.common.MentorSlotInfo;
import ru.mentor.common.MentorSlotsInfoResponse;
import ru.mentor.common.ModuleResponse;
import ru.mentor.common.PageDetails;
import ru.mentor.common.Role;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.common.UserInfo;

public class TestGrpcStubGenerator {

    public static Header constructHeader() {
        return Header.newBuilder()
                .setRequestId(TestConstantHolder.requestId)
                .setNodeId(TestConstantHolder.nodeId)
                .setApiKey(TestConstantHolder.apiKey)
                .build();
    }

    public static GetModuleRequest constructGetModuleRequest() {
        return GetModuleRequest.newBuilder()
                .setHeader(constructHeader())
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
                .setHeader(constructHeader())
                .setPageNumber(TestConstantHolder.zero)
                .setPageSize(TestConstantHolder.pageSize)
                .build();
    }

    public static GetAllModulesRequest constructGetAllModulesRequest() {
        return GetAllModulesRequest.newBuilder()
                .setHeader(constructHeader())
                .setCourseId(TestConstantHolder.courseId)
                .build();
    }

    public static GetCourseRequest constructGetCourseRequest() {
        return GetCourseRequest.newBuilder()
                .setHeader(constructHeader())
                .setCourseId(TestConstantHolder.courseId)
                .build();
    }

    public static PageDetails constructPageDetails() {
        return PageDetails.newBuilder()
                .setPage(TestConstantHolder.zero)
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
                .setIsActive(TestConstantHolder.isActiveFalse)
                .setCreatedAt(TestConstantHolder.createdAtTimestamp)
                .setCourseId(TestConstantHolder.courseId)
                .build();
    }

    public static AuthorResponse constructAuthorResponse() {
        return AuthorResponse.newBuilder()
                .setUserId(TestConstantHolder.mentorId)
                .setUsername(TestConstantHolder.mentorName)
                .setFirstName(TestConstantHolder.mentorFirstName)
                .setLastName(TestConstantHolder.mentorLastName)
                .setTgNickname(TestConstantHolder.mentorTgNickname)
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
                .setIsActive(TestConstantHolder.isActiveFalse)
                .setCreatedAt(TestConstantHolder.createdAtTimestamp)
                .setAuthor(constructAuthorResponse())
                .build();
    }

    public static TimeSlotResponse constructTimeSlotResponse() {
        return TimeSlotResponse.newBuilder()
                .setRequestId(TestConstantHolder.requestId)
                .setSlotId(TestConstantHolder.timeSlotId)
                .setMentorId(TestConstantHolder.mentorId)
                .setStartTime(TestConstantHolder.startTimestamp)
                .setEndTime(TestConstantHolder.endTimestamp)
                .setSlotType(TestConstantHolder.grpcSlotType)
                .setSlotMeetingType(TestConstantHolder.grpcSlotMeetingType)
                .setMaxParticipants(TestConstantHolder.maxParticipants)
                .setMeetingLink(TestConstantHolder.meetingLink)
                .setDescription(TestConstantHolder.slotDescription)
                .setCreatedAt(TestConstantHolder.createdAtTimestamp)
                .build();
    }

    public static UserInfo constructUserInfo(long userId) {
        return UserInfo.newBuilder()
                .setId(userId)
                .setUsername(TestConstantHolder.username)
                .setRole(Role.USER)
                .setFirstName(TestConstantHolder.userFirstName)
                .setLastName(TestConstantHolder.userLastName)
                .setTgNickname(TestConstantHolder.userTgNickname)
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

    public static CreateCourseGrpcRequest constructCreateCourseGrpcRequest() {
        return CreateCourseGrpcRequest.newBuilder()
                .setHeader(constructHeader())
                .setUserId(TestConstantHolder.userId)
                .setCourseName(TestConstantHolder.courseTitle)
                .setCourseDescription(TestConstantHolder.courseDescription)
                .build();
    }

    public static DeleteCourseRequest constructDeleteCourseRequest() {
        return DeleteCourseRequest.newBuilder()
                .setHeader(constructHeader())
                .setSenderId(TestConstantHolder.userId)
                .setCourseId(TestConstantHolder.courseId)
                .build();
    }

    public static CreateModuleGrpcRequest constructCreateModuleGrpcRequest() {
        return CreateModuleGrpcRequest.newBuilder()
                .setHeader(constructHeader())
                .setCourseId(TestConstantHolder.courseId)
                .setContent(TestConstantHolder.moduleContent)
                .setTitle(TestConstantHolder.moduleTitle)
                .setOrderNumber(TestConstantHolder.moduleOrderNumber)
                .build();
    }

    public static ImportModuleFromFileRequest constructImportModuleFromFileGrpcRequest() {
        return ImportModuleFromFileRequest.newBuilder()
                .setHeader(constructHeader())
                .setCourseId(TestConstantHolder.courseId)
                .setContent(TestConstantHolder.moduleContent)
                .setTitle(TestConstantHolder.moduleTitle)
                .setOrderNumber(TestConstantHolder.moduleOrderNumber)
                .setFileContent(ByteString.copyFrom(TestConstantHolder.moduleContent.getBytes()))
                .build();
    }

    public static DeleteModuleRequest constructDeleteModuleRequest() {
        return DeleteModuleRequest.newBuilder()
                .setHeader(constructHeader())
                .setSenderId(TestConstantHolder.userId)
                .setCourseId(TestConstantHolder.courseId)
                .setModuleId(TestConstantHolder.moduleId)
                .setModuleOrderNumber(TestConstantHolder.moduleOrderNumber)
                .build();
    }

    public static CreateCourseTagGrpcRequest constructCreateCourseTagGrpcRequest(){
        return CreateCourseTagGrpcRequest.newBuilder()
                .setHeader(constructHeader())
                .setSenderId(TestConstantHolder.userId)
                .setName(TestConstantHolder.courseTagName)
                .build();
    }

    public static CourseTagResponse constructCourseTagResponse(){
        return CourseTagResponse.newBuilder()
                .setId(TestConstantHolder.courseTagId)
                .setName(TestConstantHolder.courseTagName)
                .setCreatedAt(TestConstantHolder.createdAtTimestamp)
                .setIsActive(TestConstantHolder.isActiveTrue)
                .build();
    }

    public static DeleteCourseTagRequest constructDeleteCourseTagGrpcRequest(){
        return DeleteCourseTagRequest.newBuilder()
                .setHeader(constructHeader())
                .setSenderId(TestConstantHolder.userId)
                .setTagId(TestConstantHolder.courseTagId)
                .build();
    }

    public static GetCourseTagRequest constructGetCourseTagGrpcRequest() {
        return GetCourseTagRequest.newBuilder()
                .setHeader(constructHeader())
                .setSenderId(TestConstantHolder.userId)
                .setTagId(TestConstantHolder.courseTagId)
                .build();
    }

    public static GetAllCourseTagsRequest constructGetAllCourseTagsRequest(){
        return GetAllCourseTagsRequest.newBuilder()
                .setHeader(constructHeader())
                .setSenderId(TestConstantHolder.userId)
                .build();
    }

    public static ListCourseTagsResponse constructAllCourseTagsResponse(){
        return ListCourseTagsResponse.newBuilder()
                .addTags(constructCourseTagResponse())
                .build();
    }

}
