package com.reliaquest.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ResponseTest {

    @Test
    void testDefaultConstructorAndSettersGetters() {
        Response<String> response = new Response<>();

        // Test setters
        response.setData("TestData");
        response.setStatus("success");
        response.setMessage("Operation completed");

        // Test getters
        assertEquals("TestData", response.getData());
        assertEquals("success", response.getStatus());
        assertEquals("Operation completed", response.getMessage());
    }

    @Test
    void testConstructorWithDataAndStatus() {
        Response<Integer> response = new Response<>(123, "success");

        assertEquals(123, response.getData());
        assertEquals("success", response.getStatus());
        assertNull(response.getMessage());
    }

    @Test
    void testHandledWithStaticMethod() {
        Response<String> response = Response.handledWith("Hello");

        assertEquals("Hello", response.getData());
        assertEquals("success", response.getStatus());
        assertNull(response.getMessage());
    }

    @Test
    void testSetMessageSeparately() {
        Response<String> response = Response.handledWith("Hello");
        response.setMessage("Custom message");

        assertEquals("Hello", response.getData());
        assertEquals("success", response.getStatus());
        assertEquals("Custom message", response.getMessage());
    }

    @Test
    void testGenericTypeWorks() {
        Response<Employee> response = new Response<>();
        Employee emp = new Employee();
        emp.setName("John");

        response.setData(emp);
        response.setStatus("success");

        assertNotNull(response.getData());
        assertEquals("John", response.getData().getName());
        assertEquals("success", response.getStatus());
    }
}
