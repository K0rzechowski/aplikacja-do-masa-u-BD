package emino.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import emino.backend.dto.MassageDto;
import emino.backend.exception.AccessDeniedException;
import emino.backend.models.Admin;
import emino.backend.models.Massage;
import emino.backend.models.Symptom;
import emino.backend.repositories.CompanyRepository;
import emino.backend.repositories.EmployeeRepository;
import emino.backend.repositories.symptomRepository;
import emino.backend.repositories.SymptomRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SymptomService {
    
    private final MassageRepository massageRepository;
    private final SymptomRepository symptomRepository;
    private final EmployeeRepository emplRepository;
    private final CompanyRepository companyRepository;

    
    public List<Symptom> getSymptomsByCompany(Long companyId) {
        var company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));
        return symptomRepository.findByCompany(company);
    } 

    public Optional<Symptom> getMassageBySymptom(String name) {
        return symptomRepository.findByName(name);
    } 

    public List<Symptom> getSymptomsByMassage(Long massageId) {
        var massage = massageRepository.findById(massageId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego masażu"));
        return symptomRepository.findByMassage(massage);
    } 

    // TODO for now, no employee is associated with the symptoms
    public List<Symptom> getSymptomsByEmployee(Long employeeId) {
        var employee = emplRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego pracownika"));
        return symptomRepository.findByEmployee(employee);
    } 

    public Symptom createSymptom(SymptomDto request, Admin admin) {
        var company = admin.getCompany();

        Symptom symptom = Massage.builder()
            .name(request.getName())
            .company(company)
            .build();

        if (!symptom.getCompany().equals(admin.getCompany())) {
            throw new AccessDeniedException("Nie można dodać symptomu do nie swojej firmy");
        }
        return symptomRepository.save(massage);
    }

    public void removeSymptom(Long id) {
        symptomRepository.deleteById(id);
    }

    public Symptom editSymptom(Long id, SymptomDto request, Admin admin ) {
        Symptom symptom = symptomRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego symptomu"));

        if (request.getName() != null) {
            massage.setName(request.getName());
        }

        if (!symptom.getCompany().equals(admin.getCompany())) {
            throw new AccessDeniedException("Nie można dodać symptomu do nie swojej firmy");
        }
     
        return symptomRepository.save(symptom);
    }
}
