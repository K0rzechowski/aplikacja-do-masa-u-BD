package emino.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.config.SecurityService;
import emino.backend.dto.EmployeeCreateDto;
import emino.backend.dto.EmployeeDto;
import emino.backend.models.Admin;
import emino.backend.models.Employee;
import emino.backend.services.EmployeeService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class EmployeeController {
    
    private final EmployeeService emplService;
    private final SecurityService securityService;

    @PostMapping("/create")
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeCreateDto request) throws MessagingException {
        Admin admin = securityService.getCurrentAdmin();
        Employee empl = emplService.createEmployee(request, admin);
        EmployeeDto emplDto = emplService.getEmployeeResponse(empl);
        return ResponseEntity.ok(emplDto);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getEmployeesForMyCompany() {
        Admin admin = securityService.getCurrentAdmin();
        Long companyId = admin.getCompany().getId();
        List<Employee> empls = emplService.getEmployeesByCompany(companyId);
        List<EmployeeDto> emplsAlphabetically = empls.stream()
            .map(emplService::getEmployeeResponse)
            .sorted((e1, e2) -> {
                int surnameComparison = e1.getSurname().compareToIgnoreCase(e2.getSurname());
                return surnameComparison != 0 ? surnameComparison : e1.getName().compareToIgnoreCase(e2.getName());
            })
            .toList();
        return ResponseEntity.ok(emplsAlphabetically);
    }

    @GetMapping("/company/{id}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByCompany(@PathVariable Long id) {
        List<Employee> empls = emplService.getEmployeesByCompany(id);
        List<EmployeeDto> emplsAlphabetically = empls.stream()
            .map(emplService::getEmployeeResponse)
            .sorted((e1, e2) -> {
                int surnameComparison = e1.getSurname().compareToIgnoreCase(e2.getSurname());
                return surnameComparison != 0 ? surnameComparison : e1.getName().compareToIgnoreCase(e2.getName());
            })
            .toList();
        return ResponseEntity.ok(emplsAlphabetically);
    }

    @GetMapping("/all")
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        List<Employee> empls = emplService.getAllEmployees();
        List<EmployeeDto> emplsAlphabetically = empls.stream()
            .map(emplService::getEmployeeResponse)
            .sorted((e1, e2) -> {
                int surnameComparison = e1.getSurname().compareToIgnoreCase(e2.getSurname());
                return surnameComparison != 0 ? surnameComparison : e1.getName().compareToIgnoreCase(e2.getName());
            })
            .toList();
        return ResponseEntity.ok(emplsAlphabetically);
    }

    @GetMapping("/massage/{id}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByMassage(@PathVariable Long id) {
        List<Employee> empls = emplService.getEmployeesByMassage(id);
        List<EmployeeDto> emplsAlphabetically = empls.stream()
            .map(emplService::getEmployeeResponse)
            .sorted((e1, e2) -> {
                int surnameComparison = e1.getSurname().compareToIgnoreCase(e2.getSurname());
                return surnameComparison != 0 ? surnameComparison : e1.getName().compareToIgnoreCase(e2.getName());
            })
            .toList();
        return ResponseEntity.ok(emplsAlphabetically);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<EmployeeDto> editEmployee(@PathVariable Long id, @RequestBody EmployeeCreateDto request) {
        Admin admin = securityService.getCurrentAdmin();
        Employee updatedEmpl = emplService.editEmployee(id, request, admin);
        EmployeeDto emplDto = emplService.getEmployeeResponse(updatedEmpl);
        return ResponseEntity.ok(emplDto);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity removeEmployee(@PathVariable Long id) {
        emplService.removeEmployee(id);
        return ResponseEntity.ok().build();
    }
}
