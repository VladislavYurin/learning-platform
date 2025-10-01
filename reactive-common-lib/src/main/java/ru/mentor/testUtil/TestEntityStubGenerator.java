package ru.mentor.testUtil;

import ru.mentor.constant.Role;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.entity.ModuleEntity;

public final class TestEntityStubGenerator {

    private TestEntityStubGenerator() {
    }

    public static MentorTimeSlotEntity constructMentorTimeSlotEntity() {
        return MentorTimeSlotEntity.builder()
                                   .id(TestConstantHolder.SLOT_ID)
                                   .mentorId(TestConstantHolder.MENTOR_ID)
                                   .startTime(TestConstantHolder.SLOT_START_TIME)
                                   .endTime(TestConstantHolder.SLOT_END_TIME)
                                   .slotType(TestConstantHolder.SLOT_TYPE)
                                   .slotMeetingType(TestConstantHolder.SLOT_MEETING_TYPE)
                                   .maxParticipants(TestConstantHolder.MAX_PARTICIPANTS)
                                   .meetingLink(TestConstantHolder.MEETING_LINK)
                                   .description(TestConstantHolder.SLOT_DESCRIPTION)
                                   .createdAt(TestConstantHolder.CREATED_AT)
                                   .build();
    }

    public static UserEntity constructMentorUserEntity() {
        return UserEntity.builder()
                         .id(TestConstantHolder.MENTOR_ID)
                         .username("mentor-user")
                         .role(Role.MENTOR)
                         .firstName("Mentor")
                         .lastName("Owner")
                         .tgNickname("@mentor")
                         .build();
    }

    public static UserEntity constructParticipantEntity() {
        return UserEntity.builder()
                         .id(TestConstantHolder.USER_ID)
                         .username(TestConstantHolder.USERNAME)
                         .role(Role.USER)
                         .firstName(TestConstantHolder.FIRST_NAME)
                         .lastName(TestConstantHolder.LAST_NAME)
                         .tgNickname(TestConstantHolder.TG_NICKNAME)
                         .tgChatId(TestConstantHolder.TG_CHAT_ID)
                         .build();
    }

    public static CourseEntity constructCourseEntity() {
        return CourseEntity.builder()
                           .id(TestConstantHolder.COURSE_ID)
                           .courseTitle(TestConstantHolder.COURSE_TITLE)
                           .description(TestConstantHolder.COURSE_DESCRIPTION)
                           .isActive(TestConstantHolder.IS_ACTIVE_COURSE)
                           .createdAt(TestConstantHolder.CREATED_AT)
                           .authorId(TestConstantHolder.COURSE_AUTHOR_ID)
                           .build();
    }

    public static UserEntity constructAuthorUserEntity() {
        return UserEntity.builder()
                         .id(TestConstantHolder.MENTOR_ID)
                         .username(TestConstantHolder.USERNAME)
                         .role(Role.MENTOR)
                         .firstName(TestConstantHolder.FIRST_NAME)
                         .lastName(TestConstantHolder.LAST_NAME)
                         .tgNickname(TestConstantHolder.TG_NICKNAME)
                         .tgChatId(TestConstantHolder.TG_CHAT_ID)
                         .build();
    }

    public static ModuleEntity constructModuleEntity() {
        return ModuleEntity.builder()
                            .id(TestConstantHolder.MODULE_ID)
                            .moduleTitle(TestConstantHolder.MODULE_TITLE)
                            .moduleOrderNumber(TestConstantHolder.MODULE_ORDER_NUMBER)
                            .moduleContent(TestConstantHolder.MODULE_CONTENT)
                            .isActive(TestConstantHolder.IS_ACTIVE_MODULE)
                            .createdAt(TestConstantHolder.CREATED_AT)
                            .courseId(TestConstantHolder.COURSE_ID)
                            .build();
    }
}
