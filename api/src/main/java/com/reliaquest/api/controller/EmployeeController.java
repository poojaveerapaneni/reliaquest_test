package com.reliaquest.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;

import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    RestTemplate restTemplate;


    @Override
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        try {
            String serverUrl = "http://localhost:8112/api/v1/employee";
            List<Employee> employees = employeeService.fetchAllEmployees(serverUrl);
            return  ResponseEntity.ok(employees);
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    @Override
    public ResponseEntity<List> getEmployeesByNameSearch(String searchString) {
        return null;
    }

    @Override
    public ResponseEntity getEmployeeById(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return null;
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return null;
    }

    @Override
    public ResponseEntity createEmployee(Object employeeInput) {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        return null;
    }
}
