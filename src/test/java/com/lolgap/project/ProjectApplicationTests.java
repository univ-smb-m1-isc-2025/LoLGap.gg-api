package com.lolgap.project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")  // Active le profil "test"
class ProjectApplicationTests {

    @Test
    void contextLoads() {
        // Ce test vérifiera si le contexte Spring se charge avec H2
    }
}