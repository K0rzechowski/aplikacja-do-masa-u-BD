package emino.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import emino.backend.dto.MassageDto;
import emino.backend.exception.AccessDeniedException;
import emino.backend.models.Admin;
import emino.backend.models.Massage;
import emino.backend.repositories.CompanyRepository;
import emino.backend.repositories.EmployeeRepository;
import emino.backend.repositories.MassageRepository;
import emino.backend.repositories.SymptomRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MassageService {
    
    private final MassageRepository massageRepository;
    private final SymptomRepository symptomRepository;
    private final EmployeeRepository emplRepository;
    private final CompanyRepository companyRepository;

    
    public List<Massage> getMassagesByCompany(Long companyId) {
        var company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));
        return massageRepository.findByCompany(company);
    } 

    public Optional<Massage> getMassageByName(String name) {
        return massageRepository.findByName(name);
    } 

    public List<Massage> getMassagesBySymptom(Long symptomId) {
        var symptom = symptomRepository.findById(symptomId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego symptomu"));
        return massageRepository.findBySymptom(symptom);
    } 

    // TODO for now, no employee is associated with the massage
    public List<Massage> getMassagesByEmployee(Long employeeId) {
        var employee = emplRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego pracownika"));
        return massageRepository.findByEmployee(employee);
    } 

    public Massage createMassage(MassageDto request, Admin admin) {
        var company = admin.getCompany();

        Massage massage = Massage.builder()
            .name(request.getName())
            .price(request.getPrice())
            .minutes(request.getMinutes())
            .company(company)
            .build();

        if (!massage.getCompany().equals(admin.getCompany())) {
            throw new AccessDeniedException("Nie można dodać masażu do nie swojej firmy");
        }
        return massageRepository.save(massage);
    }

    public void removeMassage(Long id) {
        massageRepository.deleteById(id);
    }

    public Massage editMassage(Long id, MassageDto request, Admin admin ) {
        Massage massage = massageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego masażu"));

        if (request.getName() != null) {
            massage.setName(request.getName());
        }
        if (request.getPrice() != 0) {
            massage.setPrice(request.getPrice());
        }
        if (request.getMinutes() != 0) {
            massage.setMinutes(request.getMinutes());
        }

        if (!massage.getCompany().equals(admin.getCompany())) {
            throw new AccessDeniedException("Nie można dodać masażu do nie swojej firmy");
        }
     
        return massageRepository.save(massage);
    }
}
