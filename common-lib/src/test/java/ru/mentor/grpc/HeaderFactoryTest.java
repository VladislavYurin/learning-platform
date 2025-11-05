package ru.mentor.grpc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.mentor.common.Header;

class HeaderFactoryTest {

    private HeaderFactory factory;

    @BeforeEach
    void setUp() {
        factory = new HeaderFactory("node-1", "api-1");
    }

    @Test
    void create_usesInjectedFields_returnsHeader() {
        Header header = factory.create("rq-123");
        Assertions.assertNotNull(header);
        Assertions.assertEquals("rq-123", header.getRequestId());
        Assertions.assertEquals("node-1", header.getNodeId());
        Assertions.assertEquals("api-1", header.getApiKey());
    }

    @Test
    void create_withExplicitNodeAndApi_overridesInjected_returnsHeader() {
        Header header = factory.create("rq-456", "node-X", "api-Y");
        Assertions.assertNotNull(header);
        Assertions.assertEquals("rq-456", header.getRequestId());
        Assertions.assertEquals("node-X", header.getNodeId());
        Assertions.assertEquals("api-Y", header.getApiKey());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void create_invalidRequestId_throws(String invalidRq) {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory.create(invalidRq));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void createWithArgs_invalidRequestId_throws(String invalidRq) {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory.create(invalidRq, "node-X", "api-Y"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void createWithArgs_invalidNodeId_throws(String invalidNode) {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory.create("rq-1", invalidNode, "api-Y"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void createWithArgs_invalidApiKey_throws(String invalidApi) {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory.create("rq-1", "node-X", invalidApi));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void create_invalidInjectedNodeId_throws(String invalidNode) {
        HeaderFactory bad = new HeaderFactory(invalidNode, "api-1");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bad.create("rq-1"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void create_invalidInjectedApiKey_throws(String invalidApi) {
        HeaderFactory bad = new HeaderFactory("node-1", invalidApi);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bad.create("rq-1"));
    }
}
