package ru.mentor.factory;

import lombok.RequiredArgsConstructor;
import ru.mentor.common.Header;
import ru.mentor.config.GrpcHeaderProperties;

@RequiredArgsConstructor
public class HeaderFactory {

    private final GrpcHeaderProperties props;

    public Header create(String requestId) {
        return Header.newBuilder()
                     .setRequestId(requestId)
                     .setNodeId(props.getNodeId())
                     .setApiKey(props.getApiKey())
                     .build();
    }

}
