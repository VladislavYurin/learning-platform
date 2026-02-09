package ru.mentor.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.SmartLifecycle;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.interceptor.HeaderAuthLoggingServerInterceptor;
import ru.mentor.testUtil.TestConstantHolder;

class GrpcHeaderAutoConfigurationTest {

    private final GrpcHeaderAutoConfiguration autoConfiguration = new GrpcHeaderAutoConfiguration();
    private final String LIFECYCLE_RUNNING_AFTER_START_MSG =
            "Lifecycle must be running after start()";
    private final String LIFECYCLE_NOT_RUNNING_AFTER_STOP_MSG =
            "Lifecycle must not be running after stop()";
    private final String HEADER_FACTORY_NOT_NULL_MSG =
            "HeaderFactory bean must not be null";
    private final String HEADER_AUTH_INTERCEPTOR_NOT_NULL_MSG =
            "HeaderAuthLoggingServerInterceptor bean must not be null";

    @Test
    void grpcHeaderStartupCheck_validProps_startsAndIsRunning() {
        GrpcHeaderProperties props = new GrpcHeaderProperties();
        props.setNodeId(TestConstantHolder.NODE_ID);
        props.setApiKey(TestConstantHolder.API_KEY);
        SmartLifecycle lifecycle = autoConfiguration.grpcHeaderStartupCheck(props);
        lifecycle.start();
        Assertions.assertTrue(lifecycle.isRunning(), LIFECYCLE_RUNNING_AFTER_START_MSG);
        lifecycle.stop();
        Assertions.assertFalse(lifecycle.isRunning(), LIFECYCLE_NOT_RUNNING_AFTER_STOP_MSG);
    }

    @Test
    void grpcHeaderStartupCheck_blankNodeId_throwsException() {
        GrpcHeaderProperties props = new GrpcHeaderProperties();
        props.setNodeId(TestConstantHolder.BLANK);
        props.setApiKey(TestConstantHolder.API_KEY);
        SmartLifecycle lifecycle = autoConfiguration.grpcHeaderStartupCheck(props);
        Assertions.assertThrows(IllegalStateException.class, lifecycle::start);
    }

    @Test
    void grpcHeaderStartupCheck_blankApiKey_throwsException() {
        GrpcHeaderProperties props = new GrpcHeaderProperties();
        props.setNodeId(TestConstantHolder.NODE_ID);
        props.setApiKey(TestConstantHolder.BLANK);
        SmartLifecycle lifecycle = autoConfiguration.grpcHeaderStartupCheck(props);
        Assertions.assertThrows(IllegalStateException.class, lifecycle::start);
    }

    @Test
    void headerFactory_validProps_createsHeaderFactoryBean() {
        GrpcHeaderProperties props = new GrpcHeaderProperties();
        props.setNodeId(TestConstantHolder.NODE_ID);
        props.setApiKey(TestConstantHolder.API_KEY);
        HeaderFactory headerFactory = autoConfiguration.headerFactory(props);
        Assertions.assertNotNull(headerFactory, HEADER_FACTORY_NOT_NULL_MSG);
    }

    @Test
    void headerAuthLoggingServerInterceptor_validProps_createsInterceptorBean() {
        GrpcHeaderProperties props = new GrpcHeaderProperties();
        props.setNodeId(TestConstantHolder.NODE_ID);
        props.setApiKey(TestConstantHolder.API_KEY);
        HeaderAuthLoggingServerInterceptor interceptor =
                autoConfiguration.headerAuthLoggingServerInterceptor(props);
        Assertions.assertNotNull(
                interceptor, HEADER_AUTH_INTERCEPTOR_NOT_NULL_MSG
        );
    }

}