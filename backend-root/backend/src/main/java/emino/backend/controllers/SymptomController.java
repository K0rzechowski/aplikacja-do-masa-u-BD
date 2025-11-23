package emino.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.dto.SymptomDto;
import emino.backend.models.Symptom;
import emino.backend.services.SymptomService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/symptoms")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class SymptomController {
    
    private final SymptomService massageService;
    private final SymptomService symptomService;

    @PostMapping("/create")
    public ResponseEntity<SymptomDto> createSymptom(@RequestBody SymptomDto request) {
        Symptom symptom = symptomService.createSymptom(request);
        SymptomDto dto = symptomService.getSymptomResponse(symptom);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<SymptomDto>> getAllSymptoms() {
        List<Symptom> symptom = symptomService.getAll();
        List<SymptomDto> symptomsAlphabetically = symptom.stream()
            .sorted((e1, e2) -> {
                return e1.getName().compareToIgnoreCase(e2.getName());
            })
            .map(symptomService::getSymptomResponse)
            .toList();
        return  ResponseEntity.ok(symptomsAlphabetically);
    }

    @GetMapping("/massage/{id}")
    public ResponseEntity<List<SymptomDto>> getSymptomsByMassage(@PathVariable Long id) {
        List<Symptom> symptom = symptomService.getSymptomsByMassage(id);
        List<SymptomDto> symptomsAlphabetically = symptom.stream()
            .sorted((e1, e2) -> {
                return e1.getName().compareToIgnoreCase(e2.getName());
            })
            .map(symptomService::getSymptomResponse)
            .toList();
        return ResponseEntity.ok(symptomsAlphabetically);
    }
    
    @PutMapping("/edit/{id}")
    public ResponseEntity<SymptomDto> editSymptom(@PathVariable Long id, @RequestBody SymptomDto request) {
        Symptom updatedSymptom = symptomService.editSymptom(id, request);
        SymptomDto dto = symptomService.getSymptomResponse(updatedSymptom);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity removeSymptom(@PathVariable Long id) {
        massageService.removeSymptom(id);
        return ResponseEntity.ok().build();
    }
}
