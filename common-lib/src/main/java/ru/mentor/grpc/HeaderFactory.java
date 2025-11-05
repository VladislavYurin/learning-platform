package ru.mentor.grpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.mentor.common.Header;

@Component
public class HeaderFactory {

    private final String nodeId;
    private final String apiKey;

    public HeaderFactory(
            @Value("${service.node-id}") String nodeId,
            @Value("${security.api-key}") String apiKey
    ) {
        this.nodeId = nodeId;
        this.apiKey = apiKey;
    }

    public Header create(String requestId) {
        return buildHeader(requestId, nodeId, apiKey);
    }

    public Header create(String requestId, String nodeId, String apiKey) {
        return buildHeader(requestId, nodeId, apiKey);
    }

    private Header buildHeader(String requestId, String nodeId, String apiKey) {
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("requestId must not be null or blank");
        }
        if (nodeId == null || nodeId.isBlank()) {
            throw new IllegalArgumentException("nodeId must not be null or blank");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("apiKey must not be null or blank");
        }

        return Header.newBuilder()
                .setRequestId(requestId)
                .setNodeId(nodeId)
                .setApiKey(apiKey)
                .build();
    }
}
