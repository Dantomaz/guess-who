package com.myapp.guess_who.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.session.Session;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@EnableWebSocketMessageBroker
@Configuration
public class WebSocketConfig extends AbstractSessionWebSocketMessageBrokerConfigurer<Session> {

    private static final long HEARTBEAT_INTERVAL_IN_MS = 25000; // 25 seconds
    @Value("${custom.client.url}")
    private String clientUrl;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue")
            .setHeartbeatValue(new long[]{HEARTBEAT_INTERVAL_IN_MS, HEARTBEAT_INTERVAL_IN_MS})
            .setTaskScheduler(taskScheduler());
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    protected void configureStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins(clientUrl);
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
