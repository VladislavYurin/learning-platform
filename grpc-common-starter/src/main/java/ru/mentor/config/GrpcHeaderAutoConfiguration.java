package ru.mentor.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.interceptor.HeaderAuthLoggingServerInterceptor;

@AutoConfiguration
@EnableConfigurationProperties(GrpcHeaderProperties.class)
public class GrpcHeaderAutoConfiguration {

    @Bean
    public SmartLifecycle grpcHeaderStartupCheck(GrpcHeaderProperties props) {
        return new SmartLifecycle() {
            private volatile boolean running;

            @Override
            public void start() {
                String nodeId = props.getNodeId();
                String apiKey = props.getApiKey();
                if (nodeId == null || nodeId.isBlank() || apiKey == null || apiKey.isBlank()) {
                    throw new IllegalStateException(
                            "Required configuration is missing: nodeId/apiKey");
                }
                running = true;
            }

            @Override
            public void stop() {running = false;}

            @Override
            public boolean isRunning() {return running;}
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public HeaderFactory headerFactory(GrpcHeaderProperties props) {
        return new HeaderFactory(props);
    }

    @Bean
    @ConditionalOnMissingBean
    public HeaderAuthLoggingServerInterceptor headerAuthLoggingServerInterceptor(
            GrpcHeaderProperties props) {
        return new HeaderAuthLoggingServerInterceptor(props);
    }

}
