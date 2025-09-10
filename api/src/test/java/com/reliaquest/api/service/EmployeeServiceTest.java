package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.Response;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(cacheManager.getCache("employees")).thenReturn(cache);
        // set a dummy serverUrl
        employeeService.serverUrl = "http://dummyserver/employees";
    }

    @Test
    void testFetchAllEmployees() throws Exception {
        UUID uuid = UUID.randomUUID();
        String jsonResponse =
                "{\"data\":[{\"id\":%s,\"name\":\"John\",\"salary\":100,\"age\":30,\"title\":\"Tester\",\"email\":\"john@example.com\"}]}"
                        .formatted(uuid);
        System.out.println(jsonResponse);
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode dataNode = mock(JsonNode.class);

        when(restTemplate.getForObject(employeeService.serverUrl, String.class)).thenReturn(jsonResponse);
        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.get("data")).thenReturn(dataNode);
        when(dataNode.isArray()).thenReturn(true);
        List<Employee> employees = Arrays.asList(new Employee(uuid, "John", 100, 30, "Tester", "john@example.com"));
        when(objectMapper.readValue(dataNode.toString(), new TypeReference<List<Employee>>() {}))
                .thenReturn(employees);

        List<Employee> result = employeeService.fetchAllEmployees();
        assertEquals(result, employees);
    }

    @Test
    void testGetEmployeeById() throws Exception {
        String id = "1";
        String jsonResponse = "{\"data\":{\"id\":1,\"name\":\"John\",\"salary\":100,\"age\":30,\"title\":\"Tester\"}}";

        when(restTemplate.getForObject(employeeService.serverUrl + "/" + id, String.class))
                .thenReturn(jsonResponse);

        JsonNode rootNode = mock(JsonNode.class);
        JsonNode dataNode = mock(JsonNode.class);
        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.get("data")).thenReturn(dataNode);

        Employee employee = new Employee();
        employee.setId(UUID.randomUUID());
        employee.setName("John");
        when(objectMapper.treeToValue(dataNode, Employee.class)).thenReturn(employee);

        Employee result = employeeService.getEmployeeById(id);
        assertNotNull(result);
        assertEquals("John", result.getName());
    }

    @Test
    void testCreateEmployee_Success() {
        // Arrange
        Employee emp = new Employee(UUID.randomUUID(), "John", 5000, 30, "Tester", "john@example.com");
        Response<Employee> responseWrapper = Response.handledWith(emp);

        ResponseEntity<Response<Employee>> responseEntity = ResponseEntity.ok(responseWrapper);

        when(restTemplate.exchange(
                        employeeService.serverUrl,
                        HttpMethod.POST,
                        any(HttpEntity.class),
                        ArgumentMatchers.<ParameterizedTypeReference<Response<Employee>>>any()))
                .thenReturn(responseEntity);

        // Act
        Employee result = employeeService.createEmployee(emp);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals(5000, result.getSalary());
        assertEquals("Tester", result.getTitle());
    }

    @Test
    void testDeleteEmployee_Success() throws Exception {
        UUID uuid = UUID.randomUUID();
        Employee emp = new Employee(uuid, "John", 5000, 30, "Tester", "john@example.com");

        // Mock getEmployeeById
        when(employeeService.getEmployeeById(uuid.toString())).thenReturn(emp);

        // Mock RestTemplate DELETE response
        ResponseEntity<String> mockResponse = ResponseEntity.ok("John");
        when(restTemplate.exchange(employeeService.serverUrl, HttpMethod.DELETE, any(HttpEntity.class), String.class))
                .thenReturn(mockResponse);

        String result = employeeService.deleteEmployee(uuid.toString());

        assertEquals("John", result);
    }

    @Test
    void testGetEmployeeWithHighestSalary() throws Exception {
        Employee e1 = new Employee();
        e1.setSalary(100);
        Employee e2 = new Employee();
        e2.setSalary(200);

        EmployeeService spyService = spy(employeeService);
        doReturn(Arrays.asList(e1, e2)).when(spyService).fetchAllEmployees();

        Integer maxSalary = spyService.getEmployeeWithHighestSalary();
        assertEquals(200, maxSalary);
    }

    @Test
    void testGetTopTeamHighestEarningEmployeesName() throws Exception {
        Employee e1 = new Employee();
        e1.setSalary(100);
        e1.setName("A");
        Employee e2 = new Employee();
        e2.setSalary(200);
        e2.setName("B");
        Employee e3 = new Employee();
        e3.setSalary(150);
        e3.setName("C");

        EmployeeService spyService = spy(employeeService);
        doReturn(Arrays.asList(e1, e2, e3)).when(spyService).fetchAllEmployees();

        List<String> topNames = spyService.getTopTeamHighestEarningEmployeesName();
        assertEquals(Arrays.asList("B", "C", "A"), topNames);
    }
}
