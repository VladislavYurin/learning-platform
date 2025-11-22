package ru.mentor.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.mentor.common.Header;
import ru.mentor.config.GrpcHeaderProperties;
import ru.mentor.testUtil.TestConstantHolder;

class HeaderFactoryTest {

    @Test
    void create_validProps_buildsHeaderWithRequestIdAndPropsValues() {
        GrpcHeaderProperties props = new GrpcHeaderProperties();
        props.setNodeId(TestConstantHolder.NODE_ID);
        props.setApiKey(TestConstantHolder.API_KEY);

        HeaderFactory factory = new HeaderFactory(props);
        String requestId = TestConstantHolder.REQUEST_ID;

        Header header = factory.create(requestId);

        Assertions.assertEquals(
                requestId, header.getRequestId(),
                "requestId must match argument"
        );
        Assertions.assertEquals(
                TestConstantHolder.NODE_ID, header.getNodeId(),
                "nodeId must be taken from props"
        );
        Assertions.assertEquals(
                TestConstantHolder.API_KEY, header.getApiKey(),
                "apiKey must be taken from props"
        );
    }

}
