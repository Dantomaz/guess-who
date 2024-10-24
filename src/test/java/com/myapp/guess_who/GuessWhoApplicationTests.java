package com.myapp.guess_who;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@Disabled
class GuessWhoApplicationTests {

    @Test
    void contextLoads() {
    }
}
