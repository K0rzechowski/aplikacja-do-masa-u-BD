package emino.backend.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import emino.backend.dto.EmployeeCreateDto;
import emino.backend.dto.EmployeeDto;
import emino.backend.dto.MassageDto;
import emino.backend.exception.AccessDeniedException;
import emino.backend.mail.EmailService;
import emino.backend.mail.GenerateCustomPassword;
import emino.backend.models.Admin;
import emino.backend.models.Employee;
import emino.backend.models.MassageForCompany;
import emino.backend.models.UserRole;
import emino.backend.repositories.CompanyRepository;
import emino.backend.repositories.EmployeeRepository;
import emino.backend.repositories.MassageForCompanyRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    
    private final EmployeeRepository emplRepository;
    private final CompanyRepository companyRepository;
    private final MassageForCompanyRepository massageRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final GenerateCustomPassword passwordGenerator;
    private final AdminService adminService;
    
    public Optional<Employee> getEmployeeById(Long id) {
        return emplRepository.findById(id);
    }

    public List<Employee> getEmployeesByCompany(Long companyId) {
        var company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));
        return emplRepository.findByCompany(company);
    } 

    public List<Employee> getAllEmployees() {
        return emplRepository.findAll();
    }

    public Employee createEmployee(EmployeeCreateDto request, Admin admin) throws MessagingException {
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

        if (request.getMassageIds() != null && !request.getMassageIds().isEmpty()) {
            Set<MassageForCompany> massages = new HashSet<>();
            for (Long massageId : request.getMassageIds()) {
                MassageForCompany massage = massageRepository.findById(massageId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego masażu"));

                if (!massage.getCompany().equals(admin.getCompany())) {
                    throw new AccessDeniedException("Nie można przypisać masażu innej firmy do pracownika");
                }

                massages.add(massage);
            }
            savedEmployee.setMassagesToHandle(massages);
            savedEmployee = emplRepository.save(savedEmployee);
        }

        return savedEmployee;
    }

    public void removeEmployee(Long id) {
        emplRepository.deleteById(id);
    }

    public void editEmployee(Long id, EmployeeCreateDto request, Admin admin ) {
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
     
        if (request.getMassageIds() != null) {
            Set<MassageForCompany> massages = new HashSet<>();
            for (Long massageId : request.getMassageIds()) {
                MassageForCompany massage = massageRepository.findById(massageId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego masażu"));

                if (!massage.getCompany().equals(admin.getCompany())) {
                    throw new AccessDeniedException("Nie można przypisać masażu innej firmy do pracownika");
                }

                massages.add(massage);
            }
            employee.setMassagesToHandle(massages);
        }

        if (request.getRole() != null && request.getRole() == UserRole.ADMIN) {
            emplRepository.deleteById(employee.getId());
            adminService.createAdminFormEmployee(employee);
        } else {
            emplRepository.save(employee);
        }
    }

    public List<Employee> getEmployeesByMassage(Long massageId) {
        MassageForCompany massage = massageRepository.findById(massageId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego masażu"));
        
        return emplRepository.findByMassageId(massage.getId());
    }

    public EmployeeDto getEmployeeResponse(Employee employee) {
        EmployeeDto dto = new EmployeeDto();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setSurname(employee.getSurname());
        dto.setDateOfBirth(employee.getDateOfBirth());
        dto.setEmail(employee.getEmail());
        dto.setPhoneNumber(employee.getPhoneNumber());
        dto.setCompanyId(employee.getCompany().getId());
        dto.setCompanyName(employee.getCompany().getName());
        
        if (employee.getMassagesToHandle() != null) {
            List<MassageDto> massageDtos = employee.getMassagesToHandle().stream()
                .map(massage -> {
                    MassageDto massageDto = new MassageDto();
                    massageDto.setId(massage.getId());
                    massageDto.setName(massage.getMassage().getName());
                    massageDto.setPrice(massage.getPrice());
                    massageDto.setCompanyId(massage.getCompany().getId());
                    return massageDto;
                })
                .toList();
            dto.setMassages(massageDtos);
        }

        return dto;
    }
}
