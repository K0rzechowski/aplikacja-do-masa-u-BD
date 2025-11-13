package emino.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import emino.backend.dto.SymptomDto;
import emino.backend.models.Symptom;
import emino.backend.repositories.MassageForSymptomRepository;
import emino.backend.repositories.MassageRepository;
import emino.backend.repositories.SymptomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SymptomService {
    
    private final MassageRepository massageRepository;
    private final SymptomRepository symptomRepository;
    private final MassageForSymptomRepository msRepository;

    public List<Symptom> getAll() {
        return symptomRepository.findAll();
    }

    public Optional<Symptom> getMassageBySymptom(String name) {
        return symptomRepository.findByName(name);
    } 

    public List<Symptom> getSymptomsByMassage(Long massageId) {
        var massage = massageRepository.findById(massageId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego masażu"));
        return symptomRepository.findByMassage(massage);
    } 

    public Symptom createSymptom(SymptomDto request) {

        Symptom symptom = Symptom.builder()
            .name(request.getName())
            .build();

        return symptomRepository.save(symptom);
    }

    @Transactional
    public void removeSymptom(Long id) {
        msRepository.deleteBySymptomId(id);
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

    public SymptomDto getSymptomResponse(Symptom symptom) {
        SymptomDto dto = new SymptomDto();
        dto.setId(symptom.getId());
        dto.setName(symptom.getName());
        return dto;
    }
}
