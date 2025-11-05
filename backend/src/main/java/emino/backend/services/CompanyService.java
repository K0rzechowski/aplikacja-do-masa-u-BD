package emino.backend.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import emino.backend.dto.CompanyDto;
import emino.backend.exception.DuplicateCompanyException;
import emino.backend.models.Company;
import emino.backend.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
    
    private final CompanyRepository companyRepository;

    public Company createCompany(CompanyDto request) {

        if (companyRepository.findByNameAndCity(request.getName(), request.getCity()).isPresent()) {
            throw new DuplicateCompanyException("Firma o danej nazwie i danym mieście już istnieje");
        }

        Company company = Company.builder()
            .name(request.getName())
            .city(request.getCity())
            .build();

        return companyRepository.save(company);
    }
    
    public List<Company> getCompaniesByCity(String city) {
        return companyRepository.findByCity(city);
    } 

    public List<Company> getAll() {
        return companyRepository.findAll().stream()
            .sorted(Comparator
                .comparing(Company::getCity)
                .thenComparing(Company::getName))
            .collect(Collectors.toList());
    } 

    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }
}
