package emino.backend.services;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import emino.backend.dto.AdminDto;
import emino.backend.dto.EmployeeDto;
import emino.backend.mail.EmailService;
import emino.backend.mail.GenerateCustomPassword;
import emino.backend.models.Admin;
import emino.backend.models.Employee;
import emino.backend.models.UserRole;
import emino.backend.repositories.AdminRepository;
import emino.backend.repositories.CompanyRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final AdminRepository adminRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final GenerateCustomPassword passwordGenerator;

    public Admin createAdmin(AdminDto request) throws MessagingException {
        var company = companyRepository.findById(request.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));
        String generatedPassword = passwordGenerator.generatePassayPassword();

        Admin admin = Admin.adminBuilder()
            .name(request.getName())
            .surname(request.getSurname())
            .dateOfBirth(request.getDateOfBirth())
            .email(request.getEmail())
            .password(passwordEncoder.encode(generatedPassword))
            .role(UserRole.ADMIN)
            .company(company)
            .build();

        Admin savedAdmin = adminRepository.save(admin);
        emailService.sendAdminPasswordEmail(savedAdmin, generatedPassword, company.getName());
        return savedAdmin;
    }

    public Admin createAdminFormEmployee(Employee empl) {
        Admin admin = Admin.adminBuilder()
            .name(empl.getName())
            .surname(empl.getSurname())
            .dateOfBirth(empl.getDateOfBirth())
            .email(empl.getEmail())
            .password(empl.getPassword())
            .role(UserRole.ADMIN)
            .company(empl.getCompany())
            .build();
        return adminRepository.save(admin);
    }
    
    public List<Admin> getAdminsByCompany(Long companyId) {
        var company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));
        return adminRepository.findByCompany(company);
    } 
}
