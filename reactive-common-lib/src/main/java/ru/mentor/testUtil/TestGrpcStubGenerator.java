package ru.mentor.testUtil;

import com.google.protobuf.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.AuthorResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.CreateCourseGrpcRequest;
import ru.mentor.common.DeleteCourseRequest;
import ru.mentor.common.GetAllModulesRequest;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.Header;
import ru.mentor.common.MentorSlotInfo;
import ru.mentor.common.MentorTag;
import ru.mentor.common.ModuleResponse;
import ru.mentor.common.PageDetails;
import ru.mentor.common.Role;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.common.UserInfo;
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

public final class TestGrpcStubGenerator {

    private TestGrpcStubGenerator() {
    }

    private static Header buildTestHeader() {
        return Header.newBuilder()
                     .setRequestId(TestConstantHolder.REQUEST_ID)
                     .setNodeId(TestConstantHolder.NODE_ID)
                     .setApiKey(TestConstantHolder.API_KEY)
                     .build();
    }

    public static CreateCourseGrpcRequest constructCreateCourseRequest() {
        return CreateCourseGrpcRequest.newBuilder()
                                      .setHeader(buildTestHeader())
                                      .setUserId(TestConstantHolder.COURSE_AUTHOR_ID)
                                      .setCourseName(TestConstantHolder.COURSE_TITLE)
                                      .setCourseDescription(TestConstantHolder.COURSE_DESCRIPTION)
                                      .build();
    }

    public static DeleteCourseRequest constructDeleteCourseRequest() {
        return DeleteCourseRequest.newBuilder()
                                  .setHeader(buildTestHeader())
                                  .setCourseId(TestConstantHolder.COURSE_ID)
                                  .setSenderId(TestConstantHolder.COURSE_AUTHOR_ID)
                                  .build();
    }

    public static GrpcPageRequest constructGrpcPageRequest() {
        return GrpcPageRequest.newBuilder()
                              .setHeader(buildTestHeader())
                              .setPageNumber(TestConstantHolder.PAGE_NUMBER)
                              .setPageSize(TestConstantHolder.PAGE_SIZE)
                              .setSenderId(TestConstantHolder.COURSE_AUTHOR_ID)
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
                               .setRequestId(TestConstantHolder.REQUEST_ID)
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
                             .addAllTags(constructCourseTagsListResponse())
                             .addAllModules(List.of(constructModuleResponse()))
                             .setAuthor(constructCourseAuthorResponse())
                             .build();
    }

    public static CourseResponse constructCoursePreviewResponse() {
        return CourseResponse.newBuilder()
                             .setCourseId(TestConstantHolder.COURSE_ID)
                             .setTitle(TestConstantHolder.COURSE_TITLE)
                             .setDescription(TestConstantHolder.COURSE_DESCRIPTION)
                             .setIsActive(TestConstantHolder.IS_ACTIVE_COURSE)
                             .setCreatedAt(Timestamp.newBuilder()
                                                    .setSeconds(TestConstantHolder.CREATED_AT_EPOCH_SECONDS)
                                                    .build())
                             .addAllTags(constructCourseTagsListResponse())
                             .setAuthor(constructCourseAuthorResponse())
                             .build();
    }

    public static CourseResponse constructCreatedCourseResponse() {
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
                               .setHeader(buildTestHeader())
                               .setSenderId(TestConstantHolder.ADMIN_ID)
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
                               .setHeader(buildTestHeader())
                               .setCourseId(TestConstantHolder.COURSE_ID)
                               .setModuleOrderNumber(TestConstantHolder.MODULE_ORDER_NUMBER)
                               .setModuleId(TestConstantHolder.MODULE_ID)
                               .setSenderId(TestConstantHolder.COURSE_AUTHOR_ID)
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
                                   .setHeader(buildTestHeader())
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

    public static List<CourseTagResponse> constructCourseTagsListResponse() {
        List<CourseTagResponse> listOfTags = new ArrayList<>();

        for (long i = 1; i <= 4; i++) {
            listOfTags.add(CourseTagResponse.newBuilder()
                                            .setId(i)
                                            .setName("test-tag-" + i)
                                            .setCreatedAt(Timestamp.newBuilder()
                                                                   .setSeconds(TestConstantHolder.CREATED_AT_EPOCH_SECONDS)
                                                                   .build())
                                            .setIsActive(true)
                                            .build());
        }

        return listOfTags;
    }

    public static MentorTag constructMentorTag() {
        return MentorTag.newBuilder()
                        .setId(TestConstantHolder.MENTOR_TAG_ID)
                        .setName(TestConstantHolder.MENTOR_TAG_NAME_DIRECTION)
                        .setType(TestConstantHolder.MENTOR_TAG_TYPE_PROTO_DIRECTION)
                        .build();
    }

    public static AttachMentorTagsResponse constructAttachMentorTagsResponse() {
        return AttachMentorTagsResponse.newBuilder()
                                       .setRqUid(TestConstantHolder.REQUEST_ID)
                                       .addAllAttachedTagIds(TestConstantHolder.MENTOR_TAGS_IDS)
                                       .addAllNotAttachedTagIds(TestConstantHolder.MENTOR_TAGS_IDS)
                                       .build();
    }

    public static DetachMentorTagResponse constructDetachMentorTagResponse() {
        return DetachMentorTagResponse.newBuilder()
                                      .setRqUid(TestConstantHolder.REQUEST_ID)
                                      .setMentorId(TestConstantHolder.MENTOR_ID)
                                      .setTagId(TestConstantHolder.MENTOR_TAG_ID)
                                      .build();
    }

    public static MentorTagResponse constructMentorTagResponse() {
        return MentorTagResponse.newBuilder()
                                .setRqUid(TestConstantHolder.REQUEST_ID)
                                .setMentorTag(TestGrpcStubGenerator.constructMentorTag())
                                .build();
    }

    public static List<MentorTag> constructListMentorTag() {
        return List.of(constructMentorTag(), constructMentorTag());
    }

    public static AllMentorTagsResponse constructAllMentorTagsResponse() {

        return AllMentorTagsResponse.newBuilder()
                                    .setRqUid(TestConstantHolder.REQUEST_ID)
                                    .addAllAllMentorsTags(constructListMentorTag())
                                    .build();
    }

    public static MentorTagsResponse constructMentorTagsResponse() {
        return MentorTagsResponse.newBuilder()
                                 .setRqUid(TestConstantHolder.REQUEST_ID)
                                 .addAllMentorTags(constructListMentorTag())
                                 .build();
    }

    public static AttachMentorTagsRequest constructAttachMentorTagsRequest() {
        return AttachMentorTagsRequest.newBuilder()
                                      .setHeader(TestConstantHolder.HEADER)
                                      .setMentorId(TestConstantHolder.MENTOR_ID)
                                      .addAllTagIds(TestConstantHolder.MENTOR_TAGS_IDS)
                                      .build();
    }

    public static AttachMentorTagsRequest constructAttachMentorTagsRequest(Long mentorId) {
        return AttachMentorTagsRequest.newBuilder()
                                      .setHeader(TestConstantHolder.HEADER)
                                      .setMentorId(mentorId)
                                      .addAllTagIds(TestConstantHolder.MENTOR_TAGS_IDS)
                                      .build();
    }

    public static AttachMentorTagsRequest constructAttachMentorTagsRequest(Long mentorId, List<Long> mentorTagsIds) {
        return AttachMentorTagsRequest.newBuilder()
                                      .setHeader(TestConstantHolder.HEADER)
                                      .setMentorId(mentorId)
                                      .addAllTagIds(mentorTagsIds)
                                      .build();
    }

    public static CreateCustomMentorTagRequest constructCreateCustomMentorTagRequest() {
        return CreateCustomMentorTagRequest.newBuilder()
                                           .setHeader(TestConstantHolder.HEADER)
                                           .setName(TestConstantHolder.MENTOR_TAG_NAME_BADGE)
                                           .build();
    }

    public static DetachMentorTagRequest constructDetachMentorTagRequest() {
        return DetachMentorTagRequest.newBuilder()
                                     .setHeader(TestConstantHolder.HEADER)
                                     .setMentorId(TestConstantHolder.MENTOR_ID)
                                     .setTagId(TestConstantHolder.MENTOR_TAG_ID)
                                     .build();
    }

    public static DetachMentorTagRequest constructDetachMentorTagRequest(Long mentorId, Long tagId) {
        return DetachMentorTagRequest.newBuilder()
                                     .setHeader(TestConstantHolder.HEADER)
                                     .setMentorId(mentorId)
                                     .setTagId(tagId)
                                     .build();
    }

    public static AllMentorTagsRequset constructAllMentorTagsRequest() {
        return AllMentorTagsRequset.newBuilder()
                                   .setHeader(TestConstantHolder.HEADER)
                                   .build();
    }

    public static GetCurrentMentorTagsRequest constructGetCurrentMentorTagsRequest() {
        return GetCurrentMentorTagsRequest.newBuilder()
                                          .setHeader(TestConstantHolder.HEADER)
                                          .setMentorId(TestConstantHolder.MENTOR_ID)
                                          .build();
    }
}

