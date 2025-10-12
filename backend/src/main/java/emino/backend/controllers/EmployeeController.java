package emino.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.models.Employee;
import emino.backend.models.Admin;
import emino.backend.services.EmployeeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class EmployeeController {
    
    private final EmployeeService emplService;

    @PostMapping("/add")
    public ResponseEntity addEmployee(Admin admin, Employee empl) {
        emplService.createEmployee(admin, empl);
        return ResponseEntity.ok("Employee added successfully");
    }

    @GetMapping("/list")
    public List<Employee> getEmployees(@AuthenticationPrincipal Admin admin) {
        return emplService.getEmployeesByAdmin(admin);
    }
}
