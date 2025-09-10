package com.reliaquest.api.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

class ApiConfigTest {

    @Test
    void testRestTemplateBeanCreation() {
        ApiConfig config = new ApiConfig();
        RestTemplate restTemplate = config.restTemplate();

        assertNotNull(restTemplate);
        assertTrue(restTemplate instanceof RestTemplate);
    }
}
