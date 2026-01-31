package com.leets.monifit_be;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles; // ✅ 이거 필수

@SpringBootTest
@ActiveProfiles("test")
class MonifitBeApplicationTests {

    @Test
    void contextLoads() {
    }
}
