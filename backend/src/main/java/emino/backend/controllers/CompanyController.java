package emino.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.dto.CompanyRequest;
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
    public ResponseEntity<Company> createCompany(@RequestBody CompanyRequest request) {
        Company Company = companyService.createCompany(request);
        return ResponseEntity.ok(Company);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<CompanyRequest>> getAllCompanies () {
        List<Company> companies = companyService.getAll();
        List<CompanyRequest> companyRequests = companies.stream().map(company -> new CompanyRequest(company.getName(), company.getCity()))
        .toList();
        return ResponseEntity.ok(companyRequests);
    }

}
