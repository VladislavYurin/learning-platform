package ru.mentor.services.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.mentor.dto.tag.CourseTagDto;
import ru.mentor.grpc.TagsGrpcClient;
import ru.mentor.grpc.tags.ListTagsResponse;
import ru.mentor.mapper.TagGrpcMapper;
import ru.mentor.services.RedirectTagService;
import ru.mentor.util.RqGenerator;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedirectTagServiceImpl implements RedirectTagService {

    private final TagsGrpcClient client;
    private final TagGrpcMapper tagGrpcMapper;

    @Override
    public ResponseEntity<List<CourseTagDto>> getAllTags() {
        String rqId = RqGenerator.generateRqId();
        log.info("[requestId = {} ] Запрос на получение списка тегов курса", rqId);
        ListTagsResponse response = client.listTags();

        List<CourseTagDto> list = response.getTagsList().stream()
                                          .map(tagGrpcMapper::fromGrpc)
                                          .toList();

        return ResponseEntity.ok(list);
    }

}
