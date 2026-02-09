package ru.mentor.testUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.factory.Mappers;
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
import ru.mentor.entity.ModuleEntity;
import ru.mentor.mapper.BaseMapper;

import java.util.List;

public class TestEntityStubGenerator {

    private static final BaseMapper baseMapper = Mappers.getMapper(BaseMapper.class);

    public static Page<ModuleEntity> constructModuleEntityPage(ModuleEntity moduleEntity) {
        return new PageImpl<>(
                List.of(moduleEntity),
                PageRequest.of(TestConstantHolder.zero, TestConstantHolder.pageSize),
                TestConstantHolder.totalElementsCount
        );
    }

    public static ModuleEntity constructModuleEntity() {
        return ModuleEntity.builder()
                .id(TestConstantHolder.moduleId)
                .moduleTitle(TestConstantHolder.moduleTitle)
                .moduleOrderNumber(TestConstantHolder.moduleOrderNumber)
                .moduleContent(TestConstantHolder.moduleContent)
                .isActive(TestConstantHolder.isActiveFalse)
                .createdAt(TestConstantHolder.createdAt)
                .course(constructCourseEntity())
                .build();
    }

    public static ModuleDto constructModuleDto() {
        return baseMapper.moduleEntityToModuleDto(
                constructModuleEntity(),
                true);
    }

    public static String constructUserJsonWithRole(Role role) throws JsonProcessingException {
        UserInfoDto dto = UserInfoDto.builder()
                .id(TestConstantHolder.userId)
                .username(TestConstantHolder.username)
                .firstName(TestConstantHolder.userFirstName)
                .lastName(TestConstantHolder.userLastName)
                .tgNickname(TestConstantHolder.userTgNickname)
                .tgChatId(TestConstantHolder.tgChatId)
                .role(role)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(dto);
    }

    public static PageImpl<CourseEntity> constructCourseEntityPage(CourseEntity courseEntity) {
        return new PageImpl<>(
                List.of(courseEntity),
                PageRequest.of(TestConstantHolder.zero, TestConstantHolder.pageSize),
                TestConstantHolder.totalElementsCount
        );
    }

    public static UserInfoDto getMentorInfoDto() {
        return UserInfoDto.builder()
                .id(TestConstantHolder.mentorId)
                .username(TestConstantHolder.mentorName)
                .role(Role.MENTOR)
                .firstName(TestConstantHolder.mentorFirstName)
                .lastName(TestConstantHolder.mentorLastName)
                .tgNickname(TestConstantHolder.mentorTgNickname)
                .tgChatId(TestConstantHolder.tgChatId)
                .build();
    }

    public static UserInfoDto getUserInfoDto() {
        return UserInfoDto.builder()
                .id(TestConstantHolder.userId)
                .username(TestConstantHolder.username)
                .role(Role.USER)
                .firstName(TestConstantHolder.userFirstName)
                .lastName(TestConstantHolder.userLastName)
                .tgNickname(TestConstantHolder.userTgNickname)
                .tgChatId(TestConstantHolder.tgChatId)
                .build();
    }

    public static CourseEntity constructCourseEntity() {
        return CourseEntity.builder()
                .id(TestConstantHolder.courseId)
                .courseTitle(TestConstantHolder.courseTitle)
                .description(TestConstantHolder.courseDescription)
                .isActive(TestConstantHolder.isActiveFalse)
                .createdAt(TestConstantHolder.createdAt)
                .author(TestDataGenerator.getMentorEntity())
                .modules(List.of())
                .courseTags(List.of())
                .build();
    }

    public static CourseDto constructCourseDto() {
        return baseMapper.toCourseDto(
                constructCourseEntity(),
                TestDataGenerator.getMentorEntity(),
                true,
                true,
                false
        );
    }

    public static Page<CourseDto> constructCourseDtoPage() {
        CourseDto courseDto = baseMapper.toCourseDto(
                constructCourseEntity(),
                TestDataGenerator.getMentorEntity(),
                true,
                true,
                false
        );
        return new PageImpl<>(
                List.of(courseDto),
                PageRequest.of(
                        TestConstantHolder.zero,
                        TestConstantHolder.pageSize
                ),
                TestConstantHolder.totalElementsCount
        );
    }

    public static Page<MentorSlotInfoDto> constructMentorSlotInfoDtoPage() {
        List<MentorSlotInfoDto> slotInfoDtoList = List.of(constructMentorSlotInfoDto());
        return new PageImpl<>(
                slotInfoDtoList,
                PageRequest.of(
                        TestConstantHolder.zero,
                        TestConstantHolder.pageSize
                ),
                TestConstantHolder.totalElementsCount
        );
    }

    public static MentorSlotInfoDto constructMentorSlotInfoDto() {
        return MentorSlotInfoDto.builder()
                .slotDto(constructMentorTimeSlotDto())
                .participants(List.of(TestEntityStubGenerator.getUserInfoDto()))
                .build();
    }

    public static MentorTimeSlotDto constructMentorTimeSlotDto() {
        return MentorTimeSlotDto.builder()
                .id(TestConstantHolder.timeSlotId)
                .mentorId(TestConstantHolder.mentorId)
                .requestId(TestConstantHolder.requestId)
                .startTime(TestConstantHolder.startTime)
                .endTime(TestConstantHolder.endTime)
                .slotType(TestConstantHolder.slotType)
                .slotMeetingType(TestConstantHolder.slotMeetingType)
                .maxParticipants(TestConstantHolder.maxParticipants)
                .isActive(TestConstantHolder.isActiveFalse)
                .meetingLink(TestConstantHolder.meetingLink)
                .description(TestConstantHolder.slotDescription)
                .createdAt(TestConstantHolder.createdAt)
                .build();
    }

    public static Page<ModuleDto> constructModuleDtoPage() {
        List<ModuleDto> moduleDtoList = List.of(constructModuleDto());
        return new PageImpl<>(
                moduleDtoList,
                PageRequest.of(
                        TestConstantHolder.zero,
                        TestConstantHolder.pageSize
                ),
                TestConstantHolder.totalElementsCount
        );
    }

    public static MentorTimeSlotCreateRequest constructMentorTimeSlotCreateRequest() {
        return MentorTimeSlotCreateRequest.builder()
                .startTime(TestConstantHolder.startTime)
                .endTime(TestConstantHolder.endTime)
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
                .isActive(TestConstantHolder.isActiveFalse)
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
