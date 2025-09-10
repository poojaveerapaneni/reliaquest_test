package com.reliaquest.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DeleteEmployeeInputTest {

    @Test
    void testAllArgsConstructorAndGetterSetter() {
        DeleteEmployeeInput input = new DeleteEmployeeInput("John");

        // Test getter
        assertEquals("John", input.getName());

        // Test setter
        input.setName("Jane");
        assertEquals("Jane", input.getName());
    }
}
