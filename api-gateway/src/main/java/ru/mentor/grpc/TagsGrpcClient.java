package ru.mentor.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.mentor.grpc.tags.Empty;
import ru.mentor.grpc.tags.ListTagsResponse;
import ru.mentor.grpc.tags.TagsServiceGrpc;

@Component
public class TagsGrpcClient {

    @GrpcClient("course-service-client")
    private TagsServiceGrpc.TagsServiceBlockingStub stub;

    public ListTagsResponse listTags() {
        return stub.listTags(Empty.getDefaultInstance());
    }
}
