package com.reliaquest.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Mock the service layer to isolate controller
    private EmployeeService employeeService;

    @Test
    void testGetAllEmployees() throws Exception {
        Employee e1 = new Employee(UUID.randomUUID(), "John", 5000, 30, "Tester", "john@example.com");
        Employee e2 = new Employee(UUID.randomUUID(), "Jane", 6000, 28, "Developer", "jane@example.com");

        List<Employee> employees = Arrays.asList(e1, e2);
        when(employeeService.fetchAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].employee_name").value("John"))
                .andExpect(jsonPath("$[1].employee_name").value("Jane"));
    }

    @Test
    void testGetEmployeeById_Found() throws Exception {
        Employee emp = new Employee(UUID.randomUUID(), "Alice", 7000, 32, "Manager", "alice@example.com");
        when(employeeService.getEmployeeById("1")).thenReturn(emp);

        mockMvc.perform(get("/api/v1/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name").value("Alice"))
                .andExpect(jsonPath("$.employee_salary").value(7000));
    }

    @Test
    void testGetEmployeeById_NotFound() throws Exception {
        when(employeeService.getEmployeeById("999")).thenReturn(null);

        mockMvc.perform(get("/api/v1/employee/999")).andExpect(status().isNotFound());
    }

    @Test
    void testGetEmployeesByNameSearch() throws Exception {
        Employee e1 = new Employee(UUID.randomUUID(), "Jane", 6000, 28, "Developer", "jane@example.com");
        when(employeeService.getEmployeesbyName("Jane")).thenReturn(Arrays.asList(e1));

        mockMvc.perform(get("/api/v1/employee/search/Jane"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employee_name").value("Jane"));
    }

    @Test
    void testGetHighestSalary() throws Exception {
        when(employeeService.getEmployeeWithHighestSalary()).thenReturn(9000);

        mockMvc.perform(get("/api/v1/employee/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string("9000"));
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() throws Exception {
        when(employeeService.getTopTeamHighestEarningEmployeesName()).thenReturn(Arrays.asList("John", "Jane"));

        mockMvc.perform(get("/api/v1/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("John"))
                .andExpect(jsonPath("$[1]").value("Jane"));
    }

    @Test
    void testCreateEmployee() throws Exception {
        Employee emp = new Employee(UUID.randomUUID(), "NewEmp", 4000, 26, "Intern", "new@example.com");
        when(employeeService.createEmployee(org.mockito.ArgumentMatchers.any())).thenReturn(emp);

        String requestJson = "{\n" + "              \"name\": \"NewEmp\",\n"
                + "              \"salary\": 4000,\n"
                + "              \"age\": 26,\n"
                + "              \"title\": \"Intern\",\n"
                + "              \"email\": \"new@example.com\"\n"
                + "            }";

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name").value("NewEmp"))
                .andExpect(jsonPath("$.employee_salary").value(4000));
    }

    @Test
    void testDeleteEmployeeById() throws Exception {
        when(employeeService.deleteEmployee("1")).thenReturn("John");

        mockMvc.perform(delete("/api/v1/employee/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("John"));
    }
}
