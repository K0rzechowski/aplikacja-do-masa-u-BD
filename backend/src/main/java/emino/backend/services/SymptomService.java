package emino.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import emino.backend.dto.SymptomDto;
import emino.backend.models.Symptom;
import emino.backend.repositories.CompanyRepository;
import emino.backend.repositories.EmployeeRepository;
import emino.backend.repositories.MassageRepository;
import emino.backend.repositories.SymptomRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SymptomService {
    
    private final MassageRepository massageRepository;
    private final SymptomRepository symptomRepository;
    private final EmployeeRepository emplRepository;
    private final CompanyRepository companyRepository;

    public Optional<Symptom> getMassageBySymptom(String name) {
        return symptomRepository.findByName(name);
    } 

    public List<Symptom> getSymptomsByMassage(Long massageId) {
        var massage = massageRepository.findById(massageId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego masażu"));
        return symptomRepository.findByMassage(massage);
    } 

    // TODO for now, no employee is associated with the symptoms
    // public List<Symptom> getSymptomsByEmployee(Long employeeId) {
    //     var employee = emplRepository.findById(employeeId)
    //         .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego pracownika"));
    //     return symptomRepository.findByEmployee(employee);
    // } 

    public Symptom createSymptom(SymptomDto request) {

        Symptom symptom = Symptom.builder()
            .name(request.getName())
            .build();

        return symptomRepository.save(symptom);
    }

    public void removeSymptom(Long id) {
        symptomRepository.deleteById(id);
    }

    public Symptom editSymptom(Long id, SymptomDto request) {
        Symptom symptom = symptomRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego symptomu"));

        if (request.getName() != null) {
            symptom.setName(request.getName());
        }
     
        return symptomRepository.save(symptom);
    }
}
