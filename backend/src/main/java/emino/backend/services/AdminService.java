package emino.backend.services;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import emino.backend.dto.AdminDto;
import emino.backend.models.Admin;
import emino.backend.models.UserRole;
import emino.backend.repositories.AdminRepository;
import emino.backend.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final AdminRepository adminRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public Admin createAdmin(AdminDto request) {
        var company = companyRepository.findById(request.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));

        Admin admin = Admin.adminBuilder()
            .name(request.getName())
            .surname(request.getSurname())
            .dateOfBirth(request.getDateOfBirth())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(UserRole.ADMIN)
            .company(company)
            .build();

        return adminRepository.save(admin);
    }
    
    public List<Admin> getAdminsByCompany(Long companyId) {
        var company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));
        return adminRepository.findByCompany(company);
    } 
}
