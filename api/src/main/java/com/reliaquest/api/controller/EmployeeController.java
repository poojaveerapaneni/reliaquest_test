package com.reliaquest.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/employee")
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
            List<Employee> employees = employeeService.fetchAllEmployees();
            return ResponseEntity.ok(employees);
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List> getEmployeesByNameSearch(String searchString) {
        try {
            List<Employee> employeeList = employeeService.getEmployeesbyName(searchString);

            if (!employeeList.isEmpty()) {
                return ResponseEntity.ok(employeeList);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity getEmployeeById(String id) {
        Employee emp = null;
        try {
            emp = employeeService.getEmployeeById(id);
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
        return emp != null ? ResponseEntity.ok(emp) : ResponseEntity.notFound().build();
    }

    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        try {
            int salary = employeeService.getEmployeeWithHighestSalary();
            return salary > 0
                    ? ResponseEntity.ok(salary)
                    : ResponseEntity.internalServerError().build();
        } catch (Exception exception) {
            ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.internalServerError().build();
    }

    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        try {
            List<String> employeeNames = employeeService.getTopTeamHighestEarningEmployeesName();

            if (!employeeNames.isEmpty()) {
                return ResponseEntity.ok(employeeNames);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    @PostMapping()
    public ResponseEntity createEmployee(Object employeeInput) {
        Employee employeeCreated = employeeService.createEmployee(employeeInput);
        return employeeCreated != null
                ? ResponseEntity.ok(employeeCreated)
                : ResponseEntity.internalServerError().build();
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(String id) {
        String name = employeeService.deleteEmployee(id);
        return name != null
                ? ResponseEntity.ok(name)
                : ResponseEntity.internalServerError().build();
    }
}
