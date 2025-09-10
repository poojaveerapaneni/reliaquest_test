package com.reliaquest.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    @Test
    void testGettersAndSetters() {
        Employee emp = new Employee();
        UUID id = UUID.randomUUID();

        emp.setId(id);
        emp.setName("John");
        emp.setSalary(100);
        emp.setAge(30);
        emp.setTitle("Tester");
        emp.setEmail("john@example.com");

        assertEquals(id, emp.getId());
        assertEquals("John", emp.getName());
        assertEquals(100, emp.getSalary());
        assertEquals(30, emp.getAge());
        assertEquals("Tester", emp.getTitle());
        assertEquals("john@example.com", emp.getEmail());
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        Employee emp = new Employee(id, "Jane", 200, 25, "Developer", "jane@example.com");

        assertEquals(id, emp.getId());
        assertEquals("Jane", emp.getName());
        assertEquals(200, emp.getSalary());
        assertEquals(25, emp.getAge());
        assertEquals("Developer", emp.getTitle());
        assertEquals("jane@example.com", emp.getEmail());
    }

    @Test
    void testBuilder() {
        UUID id = UUID.randomUUID();
        Employee emp = Employee.builder()
                .id(id)
                .name("Alice")
                .salary(150)
                .age(28)
                .title("Manager")
                .email("alice@example.com")
                .build();

        assertEquals(id, emp.getId());
        assertEquals("Alice", emp.getName());
        assertEquals(150, emp.getSalary());
        assertEquals(28, emp.getAge());
        assertEquals("Manager", emp.getTitle());
        assertEquals("alice@example.com", emp.getEmail());

        // test toBuilder
        Employee emp2 = emp.toBuilder().name("Bob").build();
        assertEquals("Bob", emp2.getName());
        assertEquals(emp.getId(), emp2.getId());
        assertEquals(emp.getSalary(), emp2.getSalary());
    }

    @Test
    void testJsonNamingStrategy() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        UUID id = UUID.randomUUID();
        Employee emp = new Employee(id, "John", 100, 30, "Tester", "john@example.com");

        String json = objectMapper.writeValueAsString(emp);

        // Check that the custom naming strategy applied "employee_" prefix to all fields except "id"
        assertTrue(json.contains("\"id\":\"" + id + "\""));
        assertTrue(json.contains("\"employee_name\":\"John\""));
        assertTrue(json.contains("\"employee_salary\":100"));
        assertTrue(json.contains("\"employee_age\":30"));
        assertTrue(json.contains("\"employee_title\":\"Tester\""));
        assertTrue(json.contains("\"employee_email\":\"john@example.com\""));
    }
}
