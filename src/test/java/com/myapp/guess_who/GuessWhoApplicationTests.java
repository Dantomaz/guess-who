package com.myapp.guess_who;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class GuessWhoApplicationTests {

    static {
        System.setProperty("env_application_url", "http://localhost:8080");
        System.setProperty("env_client_url", "http://localhost:3000");
        System.setProperty("env_redis_host", "redis");
        System.setProperty("env_redis_port", "6379");
    }

    @Test
    void contextLoads() {
    }
}
