package ru.mentor.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.constant.MentorTagType;
import ru.mentor.dto.mentorTag.MentorTagDto;
import ru.mentor.entity.MentorTagEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.EntityAlreadyExistsException;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.exception.UserException;
import ru.mentor.mapper.MentorTagMapper;
import ru.mentor.repository.MentorTagLinkRepository;
import ru.mentor.repository.MentorTagRepository;
import ru.mentor.repository.UserRepository;

import java.util.List;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestDataGenerator;
import ru.mentor.testUtil.TestEntityStubGenerator;

@ExtendWith(MockitoExtension.class)
class MentorTagServiceImplTest {

    @Mock
    MentorTagLinkRepository linkRepository;

    @Mock
    MentorTagRepository mentorTagRepository;

    @Mock
    MentorTagMapper mapper;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    MentorTagServiceImpl service;

    @Test
    void createCustomMentorTag_tagExist_throwException() {
        Mockito.when(mentorTagRepository.existsByTagName(Mockito.anyString()))
               .thenReturn(true);

        EntityAlreadyExistsException exception = assertThrows(
                EntityAlreadyExistsException.class,
                () -> service.createCustomMentorTag(
                        TestConstantHolder.mentorTagNameBadge
                )
        );

        String expMessage = String.format(
                "Тэг с именем \"%s\" уже существует",
                TestConstantHolder.mentorTagNameBadge
        );

        Mockito.verify(mentorTagRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(mentorTagRepository, Mockito.times(1))
               .existsByTagName(Mockito.any());
        assertEquals(expMessage, exception.getMessage());

    }

    @Test
    void createCustomMentorTag_tagDostNotExist_returnDto() {
        Mockito.when(mentorTagRepository.existsByTagName(Mockito.anyString()))
               .thenReturn(false);
        Mockito.when(mapper.toMentorTagEntity(Mockito.anyString()))
               .thenReturn(TestEntityStubGenerator.constructTestMentorTagEntity());
        Mockito.when(mentorTagRepository.save(Mockito.any(MentorTagEntity.class)))
               .thenReturn(TestEntityStubGenerator.constructTestMentorTagEntity());
        Mockito.when(mapper.mentorTagEntityToDto(Mockito.any(MentorTagEntity.class)))
               .thenReturn(TestEntityStubGenerator.constructMentorTagDto());

        MentorTagDto dto = service.createCustomMentorTag(
                TestConstantHolder.mentorTagNameBadge
        );

        assertNotNull(dto.getId());
        assertEquals(
                TestEntityStubGenerator.constructTestMentorTagEntity().getTagName(),
                dto.getTagName()
        );
        assertEquals(
                TestEntityStubGenerator.constructTestMentorTagEntity().getType(),
                dto.getType()
        );
        Mockito.verify(mentorTagRepository, Mockito.times(1))
               .save(Mockito.any());
        Mockito.verify(mentorTagRepository, Mockito.times(1))
               .existsByTagName(Mockito.any());
    }

    @Test
    void getAllTags_mapsToDto() {
        Mockito.when(mentorTagRepository.findAll()).thenReturn(List.of(
                                                                       TestEntityStubGenerator.constructTestMentorTagEntity(),
                                                                       TestEntityStubGenerator.constructTestMentorTagEntity()
                                                               )
        );
        Mockito.when(mapper.mentorTagEntityToDto(Mockito.any()))
               .thenReturn(MentorTagDto.builder().build());

        List<MentorTagDto> list = service.getAllTags();

        Assertions.assertEquals(2, list.size());
        Mockito.verify(mentorTagRepository).findAll();
        Mockito.verify(mapper, Mockito.times(2))
               .mentorTagEntityToDto(Mockito.any());
    }

    @Test
    void getMentorTags_userMenotr_returnDtoList() {
        Mockito.when(userRepository.findByIdOrThrow(Mockito.anyLong()))
               .thenReturn(TestDataGenerator.getTestMentorUser());
        Mockito.when(linkRepository.findTagsByMentorId(Mockito.anyLong()))
               .thenReturn(List.of(TestEntityStubGenerator.constructTestMentorTagEntity()));
        Mockito.when(mapper.mentorTagEntityToDto(Mockito.any()))
               .thenReturn(TestEntityStubGenerator.constructMentorTagDto());

        List<MentorTagDto> tags = service.getMentorTags(
                TestConstantHolder.mentorId
        );

        Assertions.assertEquals(MentorTagType.DIRECTION, tags.getFirst().getType());
    }

    @Test
    void getMentorTags_notMentor_throwsUserException() {
        Mockito.when(userRepository.findByIdOrThrow(Mockito.anyLong()))
               .thenReturn(TestDataGenerator.getTestParticipantUser());

        Assertions.assertThrows(
                UserException.class,
                () -> service.getMentorTags(
                        TestConstantHolder.mentorId
                )
        );

        Mockito.verifyNoInteractions(linkRepository);
    }

    @Test
    void attachTags_save() {
        UserEntity userWithLinks = TestDataGenerator.getTestMentorUser();
        userWithLinks.setMentorTagLinks(TestEntityStubGenerator.constructListMentorTagLinkEntity());

        Mockito.when(userRepository.findByIdOrThrow(Mockito.anyLong()))
               .thenReturn(userWithLinks);
        Mockito.when(mentorTagRepository.findAllById(Mockito.any()))
               .thenReturn(List.of(TestEntityStubGenerator.constructTestMentorTagEntity()));
        Mockito.when(linkRepository.saveAll(Mockito.any()))
               .thenReturn(List.of(TestEntityStubGenerator.constructMentorTagLinkEntity()));

        service.attachTags(
                TestConstantHolder.mentorId,
                Set.of(TestConstantHolder.tagId)
        );

        Mockito.verify(userRepository, Mockito.times(1))
               .findByIdOrThrow(Mockito.any());
        Mockito.verify(mentorTagRepository, Mockito.times(1))
               .findAllById(Mockito.any());
        Mockito.verify(linkRepository, Mockito.times(1))
               .saveAll(Mockito.any());
    }

    @Test
    void attachTags_userIsNotMentor_throwsException() {
        Mockito.when(userRepository.findByIdOrThrow(Mockito.anyLong()))
               .thenReturn(TestDataGenerator.getTestParticipantUser());

        UserException exception = assertThrows(
                UserException.class,
                () -> service.attachTags(
                        TestConstantHolder.mentorId,
                        Set.of(TestConstantHolder.tagId)
                )
        );

        Mockito.verifyNoInteractions(linkRepository);
        Mockito.verifyNoInteractions(mentorTagRepository);
        assertEquals("Пользователь не является ментором", exception.getMessage());
    }

    @Test
    void attachTags_userNotFound_throwsException() {
        Mockito.doCallRealMethod()
               .when(userRepository)
               .findByIdOrThrow(TestConstantHolder.mentorId);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
               .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.attachTags(
                        TestConstantHolder.mentorId,
                        Set.of(TestConstantHolder.tagId)
                )
        );

        Mockito.verifyNoInteractions(linkRepository);
        Mockito.verifyNoInteractions(mentorTagRepository);
        assertEquals(String.format("Юзер с ID = 1 не найден"), exception.getMessage());
    }

    @Test
    void detachTag_notLinked_returnsFalse_noDelete() {
        Mockito.when(userRepository.findByIdOrThrow(Mockito.anyLong()))
               .thenReturn(TestDataGenerator.getTestMentorUser());

        service.detachTag(TestConstantHolder.mentorId, TestConstantHolder.tagId);

        Mockito.verify(linkRepository, Mockito.times(1))
               .deleteByMentorIdAndTagId(Mockito.any(), Mockito.any());
    }

    @Test
    void detachTag_userIsNotMentor_throwsException() {
        Mockito.when(userRepository.findByIdOrThrow(Mockito.anyLong()))
               .thenReturn(TestDataGenerator.getTestParticipantUser());

        UserException exception = assertThrows(
                UserException.class,
                () -> service.detachTag(
                        TestConstantHolder.mentorId,
                        TestConstantHolder.tagId
                )
        );

        Mockito.verifyNoInteractions(linkRepository);
        assertEquals("Пользователь не является ментором", exception.getMessage());
    }

    @Test
    void detachTag_userNotFound_throwsException() {
        Mockito.doCallRealMethod()
               .when(userRepository)
               .findByIdOrThrow(TestConstantHolder.mentorId);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
               .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.detachTag(
                        TestConstantHolder.mentorId,
                        TestConstantHolder.tagId
                )
        );

        Mockito.verifyNoInteractions(linkRepository);
        assertEquals(String.format("Юзер с ID = 1 не найден"), exception.getMessage());
    }

}