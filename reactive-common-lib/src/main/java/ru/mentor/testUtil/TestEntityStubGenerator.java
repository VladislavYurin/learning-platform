package ru.mentor.testUtil;

import java.util.ArrayList;
import java.util.List;
import ru.mentor.constant.Role;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.CourseTagEntity;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;

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

    public static List<MentorTimeSlotEntity> constructMentorTimeSlotEntityList() {

        List<MentorTimeSlotEntity> listOfSlots = new ArrayList<>();

                for (long i = TestConstantHolder.SLOT_ID; i <= TestConstantHolder.SLOT_ID + 1; i++) {
                    listOfSlots.add(MentorTimeSlotEntity
                                            .builder()
                                            .id(i)
                                            .mentorId(TestConstantHolder.MENTOR_ID)
                                            .startTime(TestConstantHolder.SLOT_START_TIME.plusHours(1))
                                            .endTime(TestConstantHolder.SLOT_END_TIME.plusHours(1))
                                            .slotType(TestConstantHolder.SLOT_TYPE)
                                            .slotMeetingType(TestConstantHolder.SLOT_MEETING_TYPE)
                                            .maxParticipants(TestConstantHolder.MAX_PARTICIPANTS)
                                            .meetingLink(TestConstantHolder.MEETING_LINK + i)
                                            .description(TestConstantHolder.SLOT_DESCRIPTION + i)
                                            .createdAt(TestConstantHolder.CREATED_AT.plusHours(i))
                                            .build()
                    );
                }

                return listOfSlots;
    }

    public static UserEntity constructMentorUserEntity() {
        return UserEntity.builder()
                         .id(TestConstantHolder.MENTOR_ID)
                         .username(TestConstantHolder.USERNAME)
                         .password(TestConstantHolder.PASSWORD)
                         .role(Role.MENTOR)
                         .firstName(TestConstantHolder.FIRST_NAME)
                         .lastName(TestConstantHolder.LAST_NAME)
                         .tgNickname(TestConstantHolder.TG_NICKNAME)
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
                           .courseTitle(TestConstantHolder.COURSE_TITLE)
                           .description(TestConstantHolder.COURSE_DESCRIPTION)
                           .isActive(TestConstantHolder.IS_ACTIVE_COURSE)
                           .createdAt(TestConstantHolder.CREATED_AT)
                           .authorId(TestConstantHolder.COURSE_AUTHOR_ID)
                           .build();
    }

    public static UserEntity constructAuthorUserEntity() {
        return UserEntity.builder()
                         .username(TestConstantHolder.USERNAME)
                         .password(TestConstantHolder.PASSWORD)
                         .role(Role.MENTOR)
                         .firstName(TestConstantHolder.FIRST_NAME)
                         .lastName(TestConstantHolder.LAST_NAME)
                         .tgNickname(TestConstantHolder.TG_NICKNAME)
                         .tgChatId(TestConstantHolder.TG_CHAT_ID)
                         .build();
    }

    public static ModuleEntity constructModuleEntity() {
        return ModuleEntity.builder()
                           .moduleTitle(TestConstantHolder.MODULE_TITLE)
                           .moduleOrderNumber(TestConstantHolder.MODULE_ORDER_NUMBER)
                           .moduleContent(TestConstantHolder.MODULE_CONTENT)
                           .isActive(TestConstantHolder.IS_ACTIVE_MODULE)
                           .createdAt(TestConstantHolder.CREATED_AT)
                           .courseId(TestConstantHolder.COURSE_ID)
                           .build();
    }

    public static CourseTagEntity constructActiveCourseTagEntity() {
        return CourseTagEntity.builder()
                              .id(TestConstantHolder.COURSE_TAG_ID)
                              .tagName(TestConstantHolder.COURSE_TAG_NAME)
                              .createdAt(TestConstantHolder.CREATED_AT)
                              .isActive(true)
                              .build();
    }

    public static List<CourseTagEntity> constructCourseTagEntityList(int numberOfTags) {
        List<CourseTagEntity> listOfTestTags = new ArrayList<>(numberOfTags);

        for (long i = 1; i <= numberOfTags; i++) {
            listOfTestTags.add(CourseTagEntity.builder()
                               .id(i)
                               .tagName("test-tag-" + i)
                               .createdAt(TestConstantHolder.CREATED_AT)
                               .isActive(true)
                               .build());
        }

        return listOfTestTags;
    }
}

