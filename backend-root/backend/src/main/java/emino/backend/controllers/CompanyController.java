package emino.backend.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.dto.CompanyDto;
import emino.backend.models.Company;
import emino.backend.services.CompanyService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class CompanyController {
    
    private final CompanyService companyService;

    @PostMapping("/create")
    public ResponseEntity<Company> createCompany(@RequestBody CompanyDto request) {
        Company Company = companyService.createCompany(request);
        return ResponseEntity.ok(Company);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<CompanyDto>> getAllCompanies () {
        List<Company> companies = companyService.getAll();
        List<CompanyDto> companyRequests = companies
            .stream()
            .map(company -> new CompanyDto(company.getId(), company.getName(), company.getCity()))
            .toList();
        return ResponseEntity.ok(companyRequests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDto> getById(@PathVariable Long id) {
        Optional<Company> company = companyService.getCompanyById(id);
        if (company.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        CompanyDto dto = new CompanyDto(company.get().getId(), company.get().getName(), company.get().getCity());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity removeCompany(@PathVariable Long id) {
        companyService.removeCompany(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/city/{cityName}")
    public ResponseEntity<List<CompanyDto>> getCompaniesByCity(@PathVariable String cityName) {
        List<Company> companies = companyService.getCompaniesByCity(cityName);
        List<CompanyDto> companyRequests = companies
            .stream()
            .map(company -> new CompanyDto(company.getId(), company.getName(), company.getCity()))
            .toList();
        return ResponseEntity.ok(companyRequests);
    }

    @GetMapping("/{city}/{name}")
    public ResponseEntity<CompanyDto> getCompaniesByNameAndCity(@PathVariable String name, @PathVariable String city) {
        Optional<Company> company = companyService.getByNameAndCity(name, city);
        if (company.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        CompanyDto dto = new CompanyDto(company.get().getId(), company.get().getName(), company.get().getCity());
        return ResponseEntity.ok(dto);
    }
}
