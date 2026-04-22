package ru.mentor.facade.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.mentor.constant.Role;
import ru.mentor.dto.mentorTag.MentorTagDto;
import ru.mentor.entity.MentorTagLinkEntity;
import ru.mentor.exception.EntityAlreadyExistsException;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.exception.UserException;
import ru.mentor.facade.MentorTagFacade;
import ru.mentor.grpc.tags.AllMentorTagsRequset;
import ru.mentor.grpc.tags.AttachMentorTagsRequest;
import ru.mentor.grpc.tags.CreateCustomMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagRequest;
import ru.mentor.mapper.MentorTagMapperReactive;
import ru.mentor.repository.MentorTagLinkRepository;
import ru.mentor.repository.MentorTagRepository;
import ru.mentor.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorTagFacadeImpl implements MentorTagFacade {

    private final MentorTagRepository mentorTagRepository;
    private final MentorTagLinkRepository mentorTagLinkRepository;
    private final UserRepository userRepository;
    private final MentorTagMapperReactive mentorTagMapperReactive;

    /**
     * Возвращает gRPC-ответ с созданным тэгом на основе входного запроса
     * {@link CreateCustomMentorTagRequest}
     *
     * @param request
     *         объект запроса с названием тэга и идентификатором запроса
     *
     * @return реактивная обёртка {@link Mono}, содержащая {@link MentorTagDto}
     */
    @Override
    public Mono<MentorTagDto> createCustomMentorTag(CreateCustomMentorTagRequest request) {
        return mentorTagRepository.existsByTagName(request.getName())
                                  .flatMap(exists -> {
                                      if (exists) {
                                          return Mono.error(new EntityAlreadyExistsException(
                                                  String.format(
                                                          "Тэг с именем \"%s\" уже существует",
                                                          request.getName()
                                                  )
                                          ));
                                      }
                                      return Mono.just(request.getName())
                                                 .map(mentorTagMapperReactive::toMentorTagEntity)
                                                 .flatMap(mentorTagRepository::save)
                                                 .map(mentorTagMapperReactive::mentorTagEntityToDto);
                                  });
    }

    /**
     * Возвращает gRPC-ответ со списком всех тэгов на основе входного запроса
     * {@link AllMentorTagsRequset}
     *
     * @param request
     *         объект запроса с идентификатором запроса
     *
     * @return реактивная обёртка {@link Mono}, содержащая {@link List<MentorTagDto>}
     */
    @Override
    public Mono<List<MentorTagDto>> allMentorTags(AllMentorTagsRequset requset) {
        return mentorTagRepository.findAll()
                                  .map(entity ->
                                               mentorTagMapperReactive.mentorTagEntityToDto(entity)
                                  )
                                  .collectList();
    }

    /**
     * Возвращает gRPC-ответ со списком ID привязанных тэгов на основе входного запроса
     * {@link AttachMentorTagsRequest}
     *
     * @param request
     *         объект запроса со списком ID тэгов для привязки, ID ментора и идентификатором запроса
     *
     * @return реактивная обёртка {@link Mono}, содержащая {@link List<Long>}
     */
    @Override
    @Transactional
    public Mono<List<Long>> attachMentorTags(AttachMentorTagsRequest request) {

        return userRepository.findById(request.getMentorId())
                             .switchIfEmpty(Mono.error(new EntityNotFoundException(String.format(
                                     "Ментор с ID = [%s] не найден",
                                     request.getMentorId()
                             ))))
                             .flatMap(user -> {
                                 if (!Role.checkIsMentor(user)) {
                                     return Mono.error(new UserException(
                                             "Пользователь не является ментором"));
                                 }

                                 Set<Long> uniqTagIds = new HashSet<>(request.getTagIdsList());

                                 return mentorTagLinkRepository.findByMentorId(user.getId())
                                                               .map(MentorTagLinkEntity::getTagId)
                                                               .collect(Collectors.toSet())
                                                               .flatMap(existingTagIds ->

                                                                                mentorTagRepository.findAllById(
                                                                                                           uniqTagIds)
                                                                                                   .filter(tag -> !existingTagIds.contains(
                                                                                                           tag.getId()))
                                                                                                   .map(tag -> MentorTagLinkEntity.builder()
                                                                                                                                  .mentorId(user.getId())
                                                                                                                                  .tagId(tag.getId())
                                                                                                                                  .build())
                                                                                                   .collectList()
                                                                                                   .flatMapMany(
                                                                                                           mentorTagLinkRepository::saveAll)
                                                                                                   .map(MentorTagLinkEntity::getTagId)
                                                                                                   .collectList()
                                                               );
                             });
    }

    /**
     * Возвращает пустой gRPC-ответ, в обертке {@link Mono}
     *
     * @param request
     *         объект запроса с ID тэга, ID ментора и идентификатором запроса
     *
     * @return реактивная обёртка {@link Mono}, содержащая {@link Void}
     */
    @Override
    @Transactional
    public Mono<Void> detachMentorTag(DetachMentorTagRequest request) {
        return mentorTagLinkRepository.deleteByMentorIdAndTagId(
                request.getMentorId(),
                request.getTagId()
        );
    }

}
