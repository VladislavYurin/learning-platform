package ru.mentor.service.impl;

import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.common.CreateCourseTagGrpcRequest;
import ru.mentor.common.DeleteCourseTagRequest;
import ru.mentor.common.DeleteCourseTagResponse;
import ru.mentor.common.GetCourseTagRequest;
import ru.mentor.common.ListCourseTagsResponse;
import ru.mentor.constant.Role;
import ru.mentor.facade.CourseTagFacade;
import ru.mentor.repository.UserRepository;
import ru.mentor.service.CourseTagService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseTagServiceImpl implements CourseTagService {

    private final CourseTagFacade courseTagFacade;
    private final UserRepository userRepository;

    @Override
    public Mono<CourseTagResponse> createCourseTag(CreateCourseTagGrpcRequest request) {
        return userRepository.findByIdOrThrow(request.getSenderId())
                             .flatMap(user -> {
                                 if (Role.checkIsMentor(user) || Role.checkIsAdmin(user)) {
                                     return Mono.just(request);
                                 } else {
                                     return Mono.error(
                                             Status.PERMISSION_DENIED
                                                     .withDescription(String.format(
                                                             "Юзер с [ ID = %d ] не имеет доступа к созданию тегов",
                                                             user.getId()
                                                     ))
                                                     .asRuntimeException()
                                     );
                                 }
                             })
                             .flatMap(req -> courseTagFacade.createCourseTag(request));
    }

    @Override
    public Mono<CourseTagResponse> getTagById(GetCourseTagRequest request) {
        return courseTagFacade.getTagById(request.getTagId());
    }

    @Override
    public Mono<ListCourseTagsResponse> getAllTags() {
        return courseTagFacade.getAllTags();
    }

    @Override
    public Mono<DeleteCourseTagResponse> deleteCourseTag(DeleteCourseTagRequest request) {
        return userRepository.findByIdOrThrow(request.getSenderId())
                             .flatMap(user -> {
                                 if (Role.checkIsMentor(user) || Role.checkIsAdmin(user)) {
                                     return Mono.just(request);
                                 } else {
                                     return Mono.error(
                                             Status.PERMISSION_DENIED
                                                     .withDescription(String.format(
                                                             "Юзер с [ ID = %d ] не имеет доступа к созданию тегов",
                                                             user.getId()
                                                     ))
                                                     .asRuntimeException()
                                     );
                                 }
                             })
                             .flatMap(req -> courseTagFacade.deleteCourseTag(req.getTagId()));
    }

}
