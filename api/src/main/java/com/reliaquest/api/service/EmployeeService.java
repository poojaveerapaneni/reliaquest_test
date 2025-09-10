package com.reliaquest.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import com.reliaquest.api.model.DeleteEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.Response;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class EmployeeService {

    @Value("${server.mock.employee.url}")
    protected String serverUrl;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired(required = true)
    CacheManager cacheManager;

    private final RateLimiter rateLimiter = RateLimiter.create(5.0);

    @Cacheable("employees")
    public List<Employee> fetchAllEmployees() throws Exception {
        // Acquire permission to proceed (blocks if needed)
        log.info("Waiting for permit...");
        rateLimiter.acquire();
        log.info("Permit acquired, calling API...");

        try {
            String jsonResponse = restTemplate.getForObject(serverUrl, String.class);
            // parse jsonResponse into List<Employee> using ObjectMapper
            return parseEmployees(jsonResponse);
        } catch (HttpClientErrorException.TooManyRequests e) {
            // Handle 429 gracefully
            log.error("Too many requests {}", e.getMessage());
            throw e;
        }
    }

    private List<Employee> parseEmployees(String jsonResponse) throws Exception {

        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode dataNode = root.get("data");
        if (dataNode != null && dataNode.isArray()) {
            return objectMapper.readValue(dataNode.toString(), new TypeReference<List<Employee>>() {});
        } else {
            log.error("No 'data' node in response");
            return Collections.emptyList();
        }
    }

    public List<Employee> getEmployeesbyName(String searchInput) throws Exception {
        List<Employee> employeeList = fetchAllEmployees();
        if (!employeeList.isEmpty()) {
            List<Employee> employeeListFiltered = employeeList.stream()
                    .filter(emp -> emp.getName().toLowerCase().contains(searchInput.toLowerCase()))
                    .toList();
            return !employeeListFiltered.isEmpty() ? employeeListFiltered : Collections.emptyList();
        }
        return Collections.emptyList();
    }

    public Employee getEmployeeById(String id) throws Exception {
        String jsonResponse = restTemplate.getForObject(serverUrl.concat("/").concat(id), String.class);
        JsonNode root = objectMapper.readTree(jsonResponse);

        // Extract the "data" node
        JsonNode dataNode = root.get("data");

        // Convert "data" to Employee object
        Employee employee = objectMapper.treeToValue(dataNode, Employee.class);

        return employee != null ? employee : null;
    }

    public Integer getEmployeeWithHighestSalary() throws Exception {
        List<Employee> employeeList = fetchAllEmployees();
        if (!employeeList.isEmpty()) {
            return employeeList.stream()
                    .collect(Collectors.maxBy(Comparator.comparing(Employee::getSalary)))
                    .get()
                    .getSalary();
        } else {
            return null;
        }
    }

    public List<String> getTopTeamHighestEarningEmployeesName() throws Exception {
        List<Employee> employeeList = fetchAllEmployees();
        if (!employeeList.isEmpty()) {
            return employeeList.stream()
                    .sorted(Comparator.comparing(Employee::getSalary).reversed())
                    .limit(10)
                    .map(emp -> emp.getName())
                    .toList();
        } else {
            return null;
        }
    }

    public Employee createEmployee(Object employeeInput) {
        ResponseEntity<Response<Employee>> response = restTemplate.exchange(
                serverUrl,
                HttpMethod.POST,
                new HttpEntity<>(employeeInput),
                new ParameterizedTypeReference<Response<Employee>>() {});

        return response.getBody() != null ? response.getBody().getData() : null;
    }

    public String deleteEmployee(String id) {
        try {
            Employee emp = getEmployeeById(id);
            if (emp == null) {
                return null;
            }

            DeleteEmployeeInput input = new DeleteEmployeeInput(emp.getName());
            HttpEntity<DeleteEmployeeInput> requestEntity = new HttpEntity<>(input);

            ResponseEntity<String> response =
                    restTemplate.exchange(serverUrl, HttpMethod.DELETE, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Employee deleted successfully: " + response.getBody());
                return emp.getName();
            } else {
                log.error("Delete failed: " + response.getStatusCode());
                return null;
            }

        } catch (Exception exception) {
            log.error("Error deleting employee: " + exception.getMessage());
            return null;
        }
    }

    // Refresh the cache every 10mins
    @Scheduled(fixedRate = 600000)
    public void refreshEmployeeCache() {
        cacheManager.getCache("employees").clear();
        log.info("Employee cache refreshed!");
    }
}
