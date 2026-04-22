package ru.mentor.testUtil;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import java.time.ZoneOffset;
import java.util.List;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.AuthorResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.common.CreateCourseGrpcRequest;
import ru.mentor.common.CreateCourseTagGrpcRequest;
import ru.mentor.common.CreateModuleGrpcRequest;
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
import ru.mentor.common.MentorTag;
import ru.mentor.common.ModuleResponse;
import ru.mentor.common.PageDetails;
import ru.mentor.common.Role;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.common.UserInfo;
import ru.mentor.dto.mentorTag.MentorTagDtoCreateRequest;
import ru.mentor.grpc.tags.AllMentorTagsRequset;
import ru.mentor.grpc.tags.AllMentorTagsResponse;
import ru.mentor.grpc.tags.AttachMentorTagsRequest;
import ru.mentor.grpc.tags.AttachMentorTagsResponse;
import ru.mentor.grpc.tags.CreateCustomMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagResponse;
import ru.mentor.grpc.tags.GetCurrentMentorTagsRequest;
import ru.mentor.grpc.tags.MentorTagResponse;
import ru.mentor.grpc.tags.MentorTagsResponse;

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
                               .setCourseId(TestConstantHolder.courseId)
                               .setModuleOrderNumber(TestConstantHolder.moduleOrderNumber)
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

    public static CreateCourseGrpcRequest constructCreateCourseGrpcRequest() {
        return CreateCourseGrpcRequest.newBuilder()
                .setHeader(buildTestHeader())
                .setUserId(TestConstantHolder.userId)
                .setCourseName(TestConstantHolder.courseTitle)
                .setCourseDescription(TestConstantHolder.courseDescription)
                .build();
    }

    public static DeleteCourseRequest constructDeleteCourseRequest() {
        return DeleteCourseRequest.newBuilder()
                .setHeader(buildTestHeader())
                .setSenderId(TestConstantHolder.userId)
                .setCourseId(TestConstantHolder.courseId)
                .build();
    }

    public static CreateModuleGrpcRequest constructCreateModuleGrpcRequest() {
        return CreateModuleGrpcRequest.newBuilder()
                .setHeader(buildTestHeader())
                .setCourseId(TestConstantHolder.courseId)
                .setContent(TestConstantHolder.moduleContent)
                .setTitle(TestConstantHolder.moduleTitle)
                .setOrderNumber(TestConstantHolder.moduleOrderNumber)
                .build();
    }

    public static ImportModuleFromFileRequest constructImportModuleFromFileGrpcRequest() {
        return ImportModuleFromFileRequest.newBuilder()
                .setHeader(buildTestHeader())
                .setCourseId(TestConstantHolder.courseId)
                .setContent(TestConstantHolder.moduleContent)
                .setTitle(TestConstantHolder.moduleTitle)
                .setOrderNumber(TestConstantHolder.moduleOrderNumber)
                .setFileContent(ByteString.copyFrom(TestConstantHolder.moduleContent.getBytes()))
                .build();
    }

    public static DeleteModuleRequest constructDeleteModuleRequest() {
        return DeleteModuleRequest.newBuilder()
                .setHeader(buildTestHeader())
                .setSenderId(TestConstantHolder.userId)
                .setCourseId(TestConstantHolder.courseId)
                .setModuleId(TestConstantHolder.moduleId)
                .setModuleOrderNumber(TestConstantHolder.moduleOrderNumber)
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

    public static CreateCourseTagGrpcRequest constructCreateCourseTagGrpcRequest(){
        return CreateCourseTagGrpcRequest.newBuilder()
                                         .setHeader(buildTestHeader())
                                         .setSenderId(TestConstantHolder.userId)
                                         .setName(TestConstantHolder.courseTagName)
                                         .build();
    }

    public static CourseTagResponse constructCourseTagResponse(){
        return CourseTagResponse.newBuilder()
                                .setId(TestConstantHolder.courseTagId)
                                .setName(TestConstantHolder.courseTagName)
                                .setCreatedAt(TestConstantHolder.courseTagCreatedAtTimestamp)
                                .setIsActive(TestConstantHolder.isActive)
                                .build();
    }

    public static DeleteCourseTagRequest constructDeleteCourseTagGrpcRequest(){
        return DeleteCourseTagRequest.newBuilder()
                .setHeader(buildTestHeader())
                .setSenderId(TestConstantHolder.userId)
                .setTagId(TestConstantHolder.courseTagId)
                .build();
    }

    public static GetCourseTagRequest constructGetCourseTagGrpcRequest() {
        return GetCourseTagRequest.newBuilder()
                .setHeader(buildTestHeader())
                .setSenderId(TestConstantHolder.userId)
                .setTagId(TestConstantHolder.courseTagId)
                .build();
    }

    public static GetAllCourseTagsRequest constructGetAllCourseTagsRequest(){
        return GetAllCourseTagsRequest.newBuilder()
                                      .setHeader(buildTestHeader())
                                      .setSenderId(TestConstantHolder.userId)
                                      .build();
    }

    public static ListCourseTagsResponse constructAllCourseTagsResponse(){
        return ListCourseTagsResponse.newBuilder()
                .addTags(constructCourseTagResponse())
                .build();
    }

    public static MentorTag constructMentorTag() {
        return MentorTag.newBuilder()
                        .setId(TestConstantHolder.mentorTagId)
                        .setName(TestConstantHolder.mentorTagNameDirection)
                        .setType(TestConstantHolder.mentorTagTypeProtoDirection)
                        .build();
    }

    public static AttachMentorTagsResponse constructAttachMentorTagsResponse() {
        return AttachMentorTagsResponse.newBuilder()
                                       .setRqUid(TestConstantHolder.requestId)
                                       .addAllAttachedTagIds(TestConstantHolder.mentorTagsIds)
                                       .addAllNotAttachedTagIds(TestConstantHolder.mentorTagsIds)
                                       .build();
    }

    public static DetachMentorTagResponse constructDetachMentorTagResponse() {
        return DetachMentorTagResponse.newBuilder()
                                      .setRqUid(TestConstantHolder.requestId)
                                      .setMentorId(TestConstantHolder.mentorTagId)
                                      .setTagId(TestConstantHolder.tagId)
                                      .build();
    }

    public static MentorTagResponse constructMentorTagResponse() {
        return MentorTagResponse.newBuilder()
                                .setRqUid(TestConstantHolder.requestId)
                                .setMentorTag(TestGrpcStubGenerator.constructMentorTag())
                                .build();
    }

    public static List<MentorTag> constructListMentorTag() {
        return List.of(constructMentorTag(), constructMentorTag());
    }

    public static AllMentorTagsResponse constructAllMentorTagsResponse() {

        return AllMentorTagsResponse.newBuilder()
                                    .setRqUid(TestConstantHolder.requestId)
                                    .addAllAllMentorsTags(constructListMentorTag())
                                    .build();
    }

    public static MentorTagsResponse constructMentorTagsResponse() {
        return MentorTagsResponse.newBuilder()
                                 .setRqUid(TestConstantHolder.requestId)
                                 .addAllMentorTags(constructListMentorTag())
                                 .build();
    }

    public static AttachMentorTagsRequest constructAttachMentorTagsRequest() {
        return AttachMentorTagsRequest.newBuilder()
                .setHeader(TestConstantHolder.header)
                .setMentorId(TestConstantHolder.mentorId)
                .addAllTagIds(TestConstantHolder.mentorTagsIds)
                .build();
    }

    public static CreateCustomMentorTagRequest constructCreateCustomMentorTagRequest() {
        return CreateCustomMentorTagRequest.newBuilder()
                .setHeader(TestConstantHolder.header)
                .setName(TestConstantHolder.mentorTagNameBadge)
                .build();
    }

    public static DetachMentorTagRequest constructDetachMentorTagRequest() {
        return DetachMentorTagRequest.newBuilder()
                .setHeader(TestConstantHolder.header)
                .setMentorId(TestConstantHolder.mentorId)
                .setTagId(TestConstantHolder.mentorTagId)
                .build();
    }

    public static AllMentorTagsRequset constructAllMentorTagsRequest() {
        return AllMentorTagsRequset.newBuilder()
                .setHeader(TestConstantHolder.header)
                .build();
    }

    public static GetCurrentMentorTagsRequest constructGetCurrentMentorTagsRequest() {
        return GetCurrentMentorTagsRequest.newBuilder()
                .setHeader(TestConstantHolder.header)
                .setMentorId(TestConstantHolder.mentorId)
                .build();
    }
}
