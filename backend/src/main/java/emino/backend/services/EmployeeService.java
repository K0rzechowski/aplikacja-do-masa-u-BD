package emino.backend.services;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import emino.backend.dto.EmployeeDto;
import emino.backend.dto.EmployeeUpdateDto;
import emino.backend.exception.AccessDeniedException;
import emino.backend.mail.EmailService;
import emino.backend.mail.GenerateCustomPassword;
import emino.backend.models.Admin;
import emino.backend.models.Employee;
import emino.backend.models.UserRole;
import emino.backend.repositories.CompanyRepository;
import emino.backend.repositories.EmployeeRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    
    private final EmployeeRepository emplRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final GenerateCustomPassword passwordGenerator;
    
    public List<Employee> getEmployeesByCompany(Long companyId) {
        var company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));
        return emplRepository.findByCompany(company);
    } 

    public List<Employee> getAllEmployees() {
        return emplRepository.findAll();
    }

    public Employee createEmployee(EmployeeDto request, Admin admin) throws MessagingException {
        var company = admin.getCompany();
        String generatedPassword = passwordGenerator.generatePassayPassword();

        Employee employee = Employee.emplBuilder()
            .name(request.getName())
            .surname(request.getSurname())
            .dateOfBirth(request.getDateOfBirth())
            .email(request.getEmail())
            .password(passwordEncoder.encode(generatedPassword))
            .phoneNumber(request.getPhoneNumber())
            .role(UserRole.EMPLOYEE)
            .company(company)
            .build();

        if (!employee.getCompany().equals(admin.getCompany())) {
            throw new AccessDeniedException("Nie można dodać pracownika do innej firmy");
        }

        Employee savedEmployee = emplRepository.save(employee); 
        emailService.sendHtmlPasswordEmail(savedEmployee, generatedPassword, company.getName());
        return savedEmployee;
    }

    public void removeEmployee(Long id) {
        emplRepository.deleteById(id);
    }

    public Employee editEmployee(Long id, EmployeeUpdateDto request, Admin admin ) {
        Employee employee = emplRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego pracownika"));

        if (request.getName() != null) {
            employee.setName(request.getName());
        }
        if (request.getSurname() != null) {
            employee.setSurname(request.getSurname());
        }
        if (request.getDateOfBirth() != null) {
            employee.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getEmail() != null) {
            employee.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            employee.setPhoneNumber(request.getPhoneNumber());
        }

        if (!employee.getCompany().equals(admin.getCompany())) {
            throw new AccessDeniedException("Nie można edytować pracownika innej firmy");
        }
     
        return emplRepository.save(employee);
    }
}
