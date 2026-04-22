package ru.mentor.service.impl;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentor.constant.Role;
import ru.mentor.dto.mentorTag.MentorTagDto;
import ru.mentor.entity.MentorTagEntity;
import ru.mentor.entity.MentorTagLinkEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.EntityAlreadyExistsException;
import ru.mentor.exception.UserException;
import ru.mentor.mapper.MentorTagMapper;
import ru.mentor.repository.MentorTagLinkRepository;
import ru.mentor.repository.MentorTagRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.service.MentorTagService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Реализация сервиса {@link MentorTagService}.
 * <p>Отвечает за бизнес-логику работы с тегами менторов:</p>
 * <ul>
 *   <li>получение полного списка тегов из справочника;</li>
 *   <li>загрузку тегов конкретного ментора с группировкой по типу (DIRECTION / BADGE);</li>
 *   <li>привязку и отвязку тегов к/от ментора с проверкой роли MENTOR;</li>
 *   <li>валидацию корректности данных и делегирование операций репозиториям.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class MentorTagServiceImpl implements MentorTagService {

    private final MentorTagLinkRepository linkRepository;
    private final MentorTagRepository mentorTagRepository;
    private final MentorTagMapper mapper;
    private final UserRepository userRepository;

    @Override
    public MentorTagDto createCustomMentorTag(String tagName) {
        existsByTagName(tagName);
        MentorTagEntity mentorTag = mapper.toMentorTagEntity(tagName);
        mentorTag = mentorTagRepository.save(mentorTag);

        return mapper.mentorTagEntityToDto(mentorTag);
    }

    /**
     * Возвращает список всех тегов менторов.
     */
    public List<MentorTagDto> getAllTags() {
        return mentorTagRepository.findAll()
                                  .stream()
                                  .map(mapper::mentorTagEntityToDto)
                                  .toList();
    }

    /**
     * Возвращает теги конкретного ментора
     */
    public List<MentorTagDto> getMentorTags(Long mentorId) {

        assertMentorExists(mentorId);

        List<MentorTagDto> tags = linkRepository.findTagsByMentorId(mentorId)
                                                .stream()
                                                .map(mapper::mentorTagEntityToDto)
                                                .toList();

        return tags;
    }

    /**
     * Привязывает указанные теги к ментору.
     *
     * <p>Правила:
     * <ul>
     *   <li>Игнорируются несуществующие tagId</li>
     *   <li>Идемпотентность: уже существующие связи не дублируются</li>
     * </ul>
     * </p>
     *
     * @param tagIds
     *         список идентификаторов тегов из справочника
     * @param mentorId
     *         идентификатор ментора (users.id_user)
     *
     * @return результат операции: какие теги привязаны, какие уже были, какие не существуют
     */
    @Transactional
    public List<MentorTagLinkEntity> attachTags(
            Long mentorId,
            Set<Long> uniqueRequestedIds) {
        UserEntity user = assertMentorExists(mentorId);

        List<Long> existTagsIds = user.getMentorTagLinks().stream()
                                      .map(tagLinks -> tagLinks.getTag().getId())
                                      .toList();

        Set<MentorTagEntity> entityForAttach = new HashSet<MentorTagEntity>(mentorTagRepository.findAllById(
                uniqueRequestedIds)).stream()
                .filter(entity -> !existTagsIds.contains(entity.getId()))
                .collect(Collectors.toSet());

        List<MentorTagLinkEntity> newLinks = entityForAttach.stream()
                                                            .map(tag -> {
                                                                return MentorTagLinkEntity.builder()
                                                                                          .mentor(user)
                                                                                          .tag(tag)
                                                                                          .build();

                                                            })
                                                            .toList();

        return linkRepository.saveAll(newLinks);
    }

    /**
     * Отвязывает указанный тег от ментора.
     *
     * @param mentorId
     *         идентификатор ментора
     * @param tagId
     *         идентификатор тега из справочника
     */
    @Transactional
    public void detachTag(Long mentorId, Long tagId) {
        assertMentorExists(mentorId);

        linkRepository.deleteByMentorIdAndTagId(mentorId, tagId);
    }

    /**
     * Проверяет, что пользователь с указанным ID существует и имеет роль MENTOR.
     * Если условие не выполняется — выбрасывает UserException.
     */
    private UserEntity assertMentorExists(long mentorId) {
        UserEntity user = userRepository.findByIdOrThrow(mentorId);

        if (!Role.checkIsMentor(user)) {
            throw new UserException("Пользователь не является ментором");
        }

        return user;
    }

    /**
     * Проверяет, что тэг с таким названием НЕ существует.
     * Если условие не выполняется — выбрасывает EntityAlreadyExistsException.
     */
    private void existsByTagName(String tagName) {
        if (mentorTagRepository.existsByTagName(tagName)) {
            throw new EntityAlreadyExistsException(
                    String.format(
                            "Тэг с именем \"%s\" уже существует",
                            tagName
                    )
            );
        }
    }

}
