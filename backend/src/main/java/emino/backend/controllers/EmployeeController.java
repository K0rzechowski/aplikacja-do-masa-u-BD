package emino.backend.controllers;

import java.io.IOException;
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
import emino.backend.dto.EmployeeDto;
import emino.backend.dto.EmployeeUpdateDto;
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
    public ResponseEntity createEmployee(@RequestBody EmployeeDto request) throws MessagingException {
        Admin admin = securityService.getCurrentAdmin();
        Employee empl = emplService.createEmployee(request, admin);
        return ResponseEntity.ok(empl);
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getEmployeesForMyCompany() {
        Admin admin = securityService.getCurrentAdmin();
        Long companyId = admin.getCompany().getId();
        List<Employee> empls = emplService.getEmployeesByCompany(companyId);
        List<Employee> emplsAlphabetically = empls.stream()
            .sorted((e1, e2) -> {
                int surnameComparison = e1.getSurname().compareToIgnoreCase(e2.getSurname());
                return surnameComparison != 0 ? surnameComparison : e1.getName().compareToIgnoreCase(e2.getName());
            })
            .toList();
        return  ResponseEntity.ok(emplsAlphabetically);
    }

    // TODO
    // @GetMapping("/{companyId}")
    // public ResponseEntity<List<Employee>> getEmployeesByCompany() {
    //     Admin admin = securityService.getCurrentAdmin();
    //     Long companyId = admin.getCompany().getId();
    //     List<Employee> empls = emplService.getEmployeesByCompany(companyId);
    //     List<Employee> emplsAlphabetically = empls.stream()
    //         .sorted((e1, e2) -> {
    //             int surnameComparison = e1.getSurname().compareToIgnoreCase(e2.getSurname());
    //             return surnameComparison != 0 ? surnameComparison : e1.getName().compareToIgnoreCase(e2.getName());
    //         })
    //         .toList();
    //     return  ResponseEntity.ok(emplsAlphabetically);
    // }

    @GetMapping("/all")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> empls = emplService.getAllEmployees();
        return  ResponseEntity.ok(empls);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity editEmployee(@PathVariable Long id, @RequestBody EmployeeUpdateDto request) {
        Admin admin = securityService.getCurrentAdmin();
        Employee updatedEmpl = emplService.editEmployee(id, request, admin);
        return ResponseEntity.ok(updatedEmpl);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity removeEmployee(@PathVariable Long id) {
        emplService.removeEmployee(id);
        return ResponseEntity.ok().build();
    }
}
