package ru.mentor.testUtil;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.mentor.constant.Role;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.MentorSlotInfoDto;
import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.dto.MentorTimeSlotInfoForUserDto;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.tag.CourseTagDto;
import ru.mentor.dto.tag.CreateCourseTagRequest;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.CourseTagEntity;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.TimeSlotMapper;

import java.util.List;
import java.util.Set;

public class TestEntityStubGenerator {

    private static final TimeSlotMapper timeSlotMapper = new TimeSlotMapper();
    private static final BaseMapper baseMapper = new BaseMapper();

    public static Page<ModuleEntity> constructModuleEntityPage(ModuleEntity moduleEntity) {
        return new PageImpl<>(
                List.of(moduleEntity),
                PageRequest.of(TestConstantHolder.pageNumber, TestConstantHolder.pageSize),
                TestConstantHolder.totalElementsCount
        );
    }

    public static ModuleEntity constructModuleEntity() {
        return ModuleEntity.builder()
                           .id(TestConstantHolder.moduleId)
                           .moduleTitle(TestConstantHolder.moduleTitle)
                           .moduleOrderNumber(TestConstantHolder.moduleOrderNumber)
                           .moduleContent(TestConstantHolder.moduleContent)
                           .isActive(TestConstantHolder.isActive)
                           .createdAt(TestConstantHolder.createdAt)
                           .course(constructCourseEntity())
                           .build();
    }

    public static ModuleDto constructModuleDto() {
        return baseMapper.mapModule(constructModuleEntity(), true);
    }

    public static UserEntity constructUserEntityWithRole(Role role) {
        return UserEntity.builder()
                         .id(TestConstantHolder.userId)
                         .username(TestConstantHolder.username)
                         .firstName(TestConstantHolder.firstName)
                         .lastName(TestConstantHolder.lastName)
                         .tgNickname(TestConstantHolder.tgNickname)
                         .tgChatId(TestConstantHolder.tgChatId)
                         .role(role)
                         .build();
    }

    public static PageImpl<CourseEntity> constructCourseEntityPage(CourseEntity courseEntity) {
        return new PageImpl<>(
                List.of(courseEntity),
                PageRequest.of(TestConstantHolder.pageNumber, TestConstantHolder.pageSize),
                TestConstantHolder.totalElementsCount
        );
    }

    public static UserInfoDto constructUserInfoDtoWithRole(Role role) {
        return UserInfoDto.builder()
                          .id(TestConstantHolder.userId)
                          .username(TestConstantHolder.username)
                          .role(role)
                          .firstName(TestConstantHolder.firstName)
                          .lastName(TestConstantHolder.lastName)
                          .tgNickname(TestConstantHolder.tgNickname)
                          .tgChatId(TestConstantHolder.tgChatId)
                          .build();
    }

    public static MentorTimeSlotEntity constructMentorTimeSlotEntity() {
        return MentorTimeSlotEntity.builder()
                                   .id(TestConstantHolder.timeSlotId)
                                   .mentor(constructUserEntityWithRole(Role.MENTOR))
                                   .startTime(TestConstantHolder.slotStartTime)
                                   .endTime(TestConstantHolder.slotEndTime)
                                   .slotType(TestConstantHolder.slotType)
                                   .slotMeetingType(TestConstantHolder.slotMeetingType)
                                   .maxParticipants(TestConstantHolder.maxParticipants)
                                   .meetingLink(TestConstantHolder.meetingLink)
                                   .description(TestConstantHolder.slotDescription)
                                   .createdAt(TestConstantHolder.createdAt)
                                   .isActive(TestConstantHolder.isActive)
                                   .meetingParticipants(Set.of(constructUserEntityWithRole(Role.USER)))
                                   .build();
    }

    public static CourseEntity constructCourseEntity() {
        return CourseEntity.builder()
                           .id(TestConstantHolder.courseId)
                           .courseTitle(TestConstantHolder.courseTitle)
                           .description(TestConstantHolder.courseDescription)
                           .isActive(TestConstantHolder.isActive)
                           .createdAt(TestConstantHolder.createdAt)
                           .author(constructUserEntityWithRole(Role.MENTOR))
                           .modules(List.of())
                           .courseTags(List.of())
                           .build();
    }

    public static CourseDto constructCourseDto() {
        return baseMapper.mapCourse(
                constructCourseEntity(),
                constructUserEntityWithRole(Role.MENTOR),
                true,
                true,
                false
        );
    }

    public static Page<CourseDto> constructCourseDtoPage() {
        CourseDto courseDto = baseMapper.mapCourse(
                constructCourseEntity(),
                constructUserEntityWithRole(Role.MENTOR),
                true,
                true,
                false
        );
        return new PageImpl<>(
                List.of(courseDto),
                PageRequest.of(
                        TestConstantHolder.pageNumber,
                        TestConstantHolder.pageSize
                ),
                TestConstantHolder.totalElementsCount
        );
    }

    public static Page<MentorSlotInfoDto> constructMentoSlotInfoDtoPage() {
        List<MentorSlotInfoDto> slotInfoDtoList = List.of(constructMentorSlotInfoDto());
        return new PageImpl<>(
                slotInfoDtoList,
                PageRequest.of(
                        TestConstantHolder.pageNumber,
                        TestConstantHolder.pageSize
                ),
                TestConstantHolder.totalElementsCount
        );
    }

    public static MentorSlotInfoDto constructMentorSlotInfoDto() {
        return MentorSlotInfoDto.builder()
                                .slotDto(constructMentorTimeSlotDto())
                                .participants(List.of(constructUserInfoDtoWithRole(Role.USER)))
                                .build();
    }

    public static MentorTimeSlotDto constructMentorTimeSlotDto() {
        return timeSlotMapper.grpcResponseToDto(
                TestGrpcStubGenerator.constructTimeSlotResponse());
    }

    public static Page<ModuleDto> constructModuleDtoPage() {
        List<ModuleDto> moduleDtoList = List.of(constructModuleDto());
        return new PageImpl<>(
                moduleDtoList,
                PageRequest.of(
                        TestConstantHolder.pageNumber,
                        TestConstantHolder.pageSize
                ),
                TestConstantHolder.totalElementsCount
        );
    }

    public static MentorTimeSlotCreateRequest constructMentorTimeSlotCreateRequest() {
        return MentorTimeSlotCreateRequest.builder()
                                          .startTime(TestConstantHolder.slotStartTime)
                                          .endTime(TestConstantHolder.slotEndTime)
                                          .slotType(TestConstantHolder.slotType)
                                          .slotMeetingType(TestConstantHolder.slotMeetingType)
                                          .maxParticipants(TestConstantHolder.maxParticipants)
                                          .meetingLink(TestConstantHolder.meetingLink)
                                          .description(TestConstantHolder.slotDescription)
                                          .build();
    }

    public static MentorTimeSlotInfoForUserDto constructMentorTimeSlotInfoForUserDto() {
        return MentorTimeSlotInfoForUserDto.builder()
                .mentorTimeSlotDto(constructMentorTimeSlotDto())
                .isSlotFull(false)
                .build();
    }

    public static CourseTagEntity constructCourseTagEntity() {
        return CourseTagEntity.builder()
                              .tagName(TestConstantHolder.courseTagName)
                              .isActive(TestConstantHolder.isActive)
                              .createdAt(TestConstantHolder.createdAt)
                              .courseTags(List.of())
                              .build();
    }

    public static CourseTagDto constructCourseTagDto(){
        return CourseTagDto.builder()
                           .tagName(TestConstantHolder.courseTagName)
                           .build();
    }

    public static List<CourseTagDto> constructCourseTagDtoListRequest(){
        return List.of(constructCourseTagDto());
    }

    public static CreateCourseTagRequest constructCreateCourseTagRequest(){
        return CreateCourseTagRequest
                .builder()
                .tagName(TestConstantHolder.courseTagName)
                .build();
    }
}
