package ru.mentor.service;

import java.util.Set;
import ru.mentor.dto.mentorTag.MentorTagDto;
import ru.mentor.entity.MentorTagLinkEntity;

import java.util.List;

/**
 * Сервис для работы с тегами менторов.
 * <p>Предоставляет операции для:</p>
 * <ul>
 *   <li>получения справочника всех тегов;</li>
 *   <li>получения тегов конкретного ментора (с группировкой по типу);</li>
 *   <li>привязки новых тегов к ментору;</li>
 *   <li>отвязки тегов от ментора.</li>
 * </ul>
 */
public interface MentorTagService {

    MentorTagDto createCustomMentorTag(String tagName);

    List<MentorTagDto> getAllTags();

    List<MentorTagDto> getMentorTags(Long mentorId);

    List<MentorTagLinkEntity> attachTags(Long mentorId, Set<Long> uniqueRequestedIds);

    void detachTag(Long mentorId, Long tagId);
}
