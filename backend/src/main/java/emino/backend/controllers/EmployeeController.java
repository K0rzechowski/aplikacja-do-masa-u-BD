package emino.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.config.SecurityService;
import emino.backend.dto.EmployeeDto;
import emino.backend.models.Admin;
import emino.backend.models.Employee;
import emino.backend.services.EmployeeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class EmployeeController {
    
    private final EmployeeService emplService;
    private final SecurityService securityService;

    @PostMapping("/create")
    public ResponseEntity createEmployee(@RequestBody EmployeeDto request) {
        Admin admin = securityService.getCurrentAdmin();
        Employee empl = emplService.createEmployee(request, admin);
        return ResponseEntity.ok(empl);
    }

    @GetMapping("/getAll")
    public List<Employee> getEmployees(@AuthenticationPrincipal Long companyId) {
        return emplService.getEmployeesByCompany(companyId);
    }

    @PutMapping("/edit")
    public ResponseEntity editEmployee(@RequestAttribute Long id, @RequestAttribute EmployeeDto request) {
        Employee empl = emplService.editEmployee(id, request);
        return ResponseEntity.ok(empl);
    }
}
