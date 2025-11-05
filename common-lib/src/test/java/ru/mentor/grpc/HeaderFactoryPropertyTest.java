package ru.mentor.grpc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mentor.common.Header;

@SpringBootTest(
        classes = HeaderFactory.class,
        properties = {
                "service.node-id=test-node",
                "security.api-key=test-api"
        }
)
class HeaderFactoryPropertyTest {

    @Autowired
    private HeaderFactory factory;

    @Test
    void create_usesProperties_returnsHeader() {
        Header header = factory.create("rq-123");
        Assertions.assertNotNull(header);
        Assertions.assertEquals("rq-123", header.getRequestId());
        Assertions.assertEquals("test-node", header.getNodeId());
        Assertions.assertEquals("test-api", header.getApiKey());
    }

    @Test
    void create_withArgs_overridesProperties_returnsHeader() {
        Header header = factory.create("rq-456", "node-X", "api-Y");
        Assertions.assertNotNull(header);
        Assertions.assertEquals("rq-456", header.getRequestId());
        Assertions.assertEquals("node-X", header.getNodeId());
        Assertions.assertEquals("api-Y", header.getApiKey());
    }
}
