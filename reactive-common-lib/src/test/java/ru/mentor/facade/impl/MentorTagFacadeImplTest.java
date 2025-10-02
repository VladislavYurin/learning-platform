package ru.mentor.facade.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.mentor.testUtil.TestConstantHolder.MENTOR_ID;
import static ru.mentor.testUtil.TestConstantHolder.MENTOR_TAG_NAME_BADGE;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructMentorTagDto;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructMentorTagLinkEntity;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructMentorUserEntityWithTagLink;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructParticipantEntity;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructTestMentorTagEntity;
import static ru.mentor.testUtil.TestGrpcStubGenerator.constructAttachMentorTagsRequest;
import static ru.mentor.testUtil.TestGrpcStubGenerator.constructCreateCustomMentorTagRequest;
import static ru.mentor.testUtil.TestGrpcStubGenerator.constructDetachMentorTagRequest;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.mentor.constant.MentorTagType;
import ru.mentor.dto.mentorTag.MentorTagDto;
import ru.mentor.entity.MentorTagEntity;
import ru.mentor.entity.MentorTagLinkEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.EntityAlreadyExistsException;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.exception.UserException;
import ru.mentor.grpc.tags.AllMentorTagsRequset;
import ru.mentor.grpc.tags.CreateCustomMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagRequest;
import ru.mentor.mapper.MentorTagMapperReactive;
import ru.mentor.repository.MentorTagLinkRepository;
import ru.mentor.repository.MentorTagRepository;
import ru.mentor.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class MentorTagFacadeImplTest {

    @Mock
    private MentorTagRepository mentorTagRepository;

    @Mock
    private MentorTagLinkRepository mentorTagLinkRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private MentorTagMapperReactive mentorTagMapperReactive;

    @InjectMocks
    private MentorTagFacadeImpl facade;

    @Nested
    @DisplayName("createCustomMentorTag")
    class CreateCustomMentorTag {

        private CreateCustomMentorTagRequest request;

        @BeforeEach
        void setUp() {
            request = constructCreateCustomMentorTagRequest();
        }

        @Test
        @DisplayName("Создаёт тэг, если он не существует")
        void shouldCreateTag_whenTagDoesNotExist() {
            MentorTagEntity entity = constructTestMentorTagEntity();
            MentorTagDto dto = constructMentorTagDto();

            when(mentorTagRepository.existsByTagName(MENTOR_TAG_NAME_BADGE)).thenReturn(Mono.just(
                    false));
            when(mentorTagMapperReactive.toMentorTagEntity(MENTOR_TAG_NAME_BADGE)).thenReturn(entity);
            when(mentorTagRepository.save(entity)).thenReturn(Mono.just(entity));
            when(mentorTagMapperReactive.mentorTagEntityToDto(entity)).thenReturn(dto);

            StepVerifier.create(facade.createCustomMentorTag(request))
                        .expectNext(dto)
                        .verifyComplete();
        }

        @Test
        @DisplayName("Возвращает ошибку, если тэг уже существует")
        void shouldReturnError_whenTagAlreadyExists() {
            when(mentorTagRepository.existsByTagName(MENTOR_TAG_NAME_BADGE)).thenReturn(Mono.just(
                    true));

            StepVerifier.create(facade.createCustomMentorTag(request))
                        .expectError(EntityAlreadyExistsException.class)
                        .verify();

            verify(mentorTagRepository, never()).save(any());
        }

    }

    @Nested
    @DisplayName("allMentorTags")
    class AllMentorTags {

        @Test
        @DisplayName("Возвращает все тэги")
        void shouldReturnAllTags() {
            MentorTagEntity e1 = constructTestMentorTagEntity();
            MentorTagEntity e2 = constructTestMentorTagEntity(
                    2L,
                    "Kotlin",
                    MentorTagType.DIRECTION
            );
            MentorTagDto d1 = constructMentorTagDto();
            MentorTagDto d2 = constructMentorTagDto(
                    2L,
                    "Kotlin",
                    MentorTagType.DIRECTION
            );

            when(mentorTagRepository.findAll()).thenReturn(Flux.just(e1, e2));
            when(mentorTagMapperReactive.mentorTagEntityToDto(e1)).thenReturn(d1);
            when(mentorTagMapperReactive.mentorTagEntityToDto(e2)).thenReturn(d2);

            StepVerifier.create(facade.allMentorTags(AllMentorTagsRequset.getDefaultInstance()))
                        .expectNextMatches(list -> list.size() == 2
                                && list.contains(d1)
                                && list.contains(d2))
                        .verifyComplete();
        }

        @Test
        @DisplayName("Возвращает пустой список, если тэгов нет")
        void shouldReturnEmptyList_whenNoTags() {
            when(mentorTagRepository.findAll()).thenReturn(Flux.empty());

            StepVerifier.create(facade.allMentorTags(AllMentorTagsRequset.getDefaultInstance()))
                        .expectNextMatches(List::isEmpty)
                        .verifyComplete();
        }

    }

        @Nested
        @DisplayName("attachMentorTags")
        class AttachMentorTags {

            @Test
            @DisplayName("Возвращает ошибку, если ментор не найден")
            void shouldReturnError_whenMentorNotFound() {
                when(userRepository.findById(MENTOR_ID)).thenReturn(Mono.empty());

                StepVerifier.create(facade.attachMentorTags(constructAttachMentorTagsRequest()))
                            .expectError(EntityNotFoundException.class)
                            .verify();
            }

            @Test
            @DisplayName("Возвращает ошибку, если пользователь не является ментором")
            void shouldReturnError_whenUserIsNotMentor() {
                when(userRepository.findById(MENTOR_ID)).thenReturn(Mono.just(
                        constructParticipantEntity()));

                StepVerifier.create(facade.attachMentorTags(constructAttachMentorTagsRequest()))
                            .expectError(UserException.class)
                            .verify();
            }

            @Test
            @DisplayName("Привязывает только новые тэги, пропуская уже привязанные")
            void shouldAttachOnlyNewTags_skippingAlreadyAttached() {
                MentorTagEntity existingTag = constructTestMentorTagEntity(
                        1L,
                        "Java",
                        MentorTagType.DIRECTION
                );
                MentorTagEntity newTag = constructTestMentorTagEntity(
                        2L,
                        "Kotlin",
                        MentorTagType.DIRECTION
                );

                MentorTagLinkEntity existingLink = constructMentorTagLinkEntity();
                UserEntity mentor = constructMentorUserEntityWithTagLink(List.of(existingLink));

                MentorTagLinkEntity newLink = MentorTagLinkEntity.builder()
                                                                 .mentorId(mentor.getId())
                                                                 .tagId(newTag.getId())
                                                                 .build();

                when(userRepository.findById(MENTOR_ID)).thenReturn(Mono.just(mentor));
                when(mentorTagRepository.findAllById(Mockito.anyIterable()))
                        .thenReturn(Flux.just(existingTag, newTag));
                when(mentorTagLinkRepository.saveAll(List.of(newLink)))
                        .thenReturn(Flux.just(newLink));
                when(mentorTagLinkRepository.findByMentorId(mentor.getId()))
                        .thenReturn(Flux.just(existingLink));

                StepVerifier.create(facade.attachMentorTags(constructAttachMentorTagsRequest(
                                    mentor.getId(),
                                    List.of(2L)
                            )))
                            .expectNextMatches(ids -> ids.equals(List.of(2L)))
                            .verifyComplete();
            }
        }

        @Nested
        @DisplayName("detachMentorTag")
        class DetachMentorTag {

            @Test
            @DisplayName("Удаляет связь ментора с тэгом и возвращает Mono<Void>")
            void shouldDeleteLink_andCompleteEmpty() {
                DetachMentorTagRequest request = constructDetachMentorTagRequest();

                when(mentorTagLinkRepository.deleteByMentorIdAndTagId(request.getMentorId(), request.getTagId()))
                        .thenReturn(Mono.empty());

                StepVerifier.create(facade.detachMentorTag(request))
                            .verifyComplete();

                verify(mentorTagLinkRepository).deleteByMentorIdAndTagId(request.getMentorId(), request.getTagId());
            }
        }
}
