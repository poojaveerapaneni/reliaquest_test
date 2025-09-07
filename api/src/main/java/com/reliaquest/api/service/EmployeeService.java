package com.reliaquest.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    private final RateLimiter rateLimiter= RateLimiter.create(5.0);

    public List<Employee> fetchAllEmployees(String url) throws Exception {
        // Acquire permission to proceed (blocks if needed)
        rateLimiter.acquire();
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            // parse jsonResponse into List<Employee> using ObjectMapper
            return parseEmployees(jsonResponse);
        } catch (HttpClientErrorException.TooManyRequests e) {
            // Handle 429 gracefully
            System.out.println("Rate limit exceeded. Try again later.");
            throw e;
        }
    }

    private List<Employee> parseEmployees(String jsonResponse) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);
        JsonNode dataNode = root.get("data");
        return mapper.readValue(dataNode.toString(), new TypeReference<List<Employee>>() {});
    }

}
