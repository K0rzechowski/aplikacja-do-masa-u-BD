package emino.backend.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import emino.backend.config.SecurityService;
import emino.backend.dto.MassageCreateDto;
import emino.backend.dto.MassageDto;
import emino.backend.dto.MassageSymptomDto;
import emino.backend.models.Admin;
import emino.backend.models.MassageForCompany;
import emino.backend.services.MassageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/massages")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class MassageController {
    
    private final SecurityService securityService;
    private final MassageService massageService;

    @PostMapping(value="/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MassageDto> createMassage(
            @ModelAttribute MassageCreateDto request,
            @RequestPart("file") MultipartFile file) throws IOException {
        Admin admin = securityService.getCurrentAdmin();
        MassageForCompany msg = massageService.createMassage(request, admin, file);
        MassageDto msgDto = massageService.getMassageResponse(msg);
        return ResponseEntity.ok(msgDto);
    }

    @GetMapping
    public ResponseEntity<List<MassageDto>> getMassagesForMyCompany() {
        Admin admin = securityService.getCurrentAdmin();
        Long companyId = admin.getCompany().getId();
        List<MassageForCompany> msgs = massageService.getMassagesByCompany(companyId);

        List<MassageDto> massagesAlphabetically = msgs.stream()
            .sorted((e1, e2) -> {
                return e1.getMassage().getName().compareToIgnoreCase(e2.getMassage().getName());
            })
            .map(massageService::getMassageResponse)
            .toList();

        return ResponseEntity.ok(massagesAlphabetically);
    }

    @GetMapping("/company/{id}")
    public ResponseEntity<List<MassageDto>> getMassagesByCompany(@PathVariable Long id) {
        List<MassageForCompany> msgs = massageService.getMassagesByCompany(id);
        List<MassageDto> massagesAlphabetically = msgs.stream()
            .sorted((e1, e2) -> {
                return e1.getMassage().getName().compareToIgnoreCase(e2.getMassage().getName());
            })
            .map(massageService::getMassageResponse)
            .toList();
        return ResponseEntity.ok(massagesAlphabetically);
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<List<MassageDto>> getMassagesByEmployee(@PathVariable Long id) {
        List<MassageForCompany> msgs = massageService.getMassagesByEmployee(id);
        List<MassageDto> massagesAlphabetically = msgs.stream()
        .sorted((e1, e2) -> {
            return e1.getMassage().getName().compareToIgnoreCase(e2.getMassage().getName());
        })
        .map(massageService::getMassageResponse)
        .toList();
    return ResponseEntity.ok(massagesAlphabetically);
    }

    @GetMapping("/symptom/{symptomId}")
    public ResponseEntity<List<MassageSymptomDto>> getMassagesBySymptom(@PathVariable Long symptomId, @RequestParam(required = false) String city) {
        List<MassageSymptomDto> msgs = massageService.getMassageSymptomAssociations(symptomId, city);
        List<MassageSymptomDto> massagesInOrder = msgs.stream()
            .sorted((m1, m2) -> Double.compare(m2.getEffectivenessScore(), m1.getEffectivenessScore()))
            .toList();

        return ResponseEntity.ok(massagesInOrder);
    }

    @GetMapping("/{city}")
    public ResponseEntity<List<MassageDto>> getMassagesByCity(@PathVariable String city) {
        List<MassageForCompany> msgs = massageService.getMassagesByCity(city);
        List<MassageDto> massagesAlphabetically = msgs.stream()
            .sorted((e1, e2) -> {
                return e1.getMassage().getName().compareToIgnoreCase(e2.getMassage().getName());
            })
            .map(massageService::getMassageResponse)
            .toList();
        return ResponseEntity.ok(massagesAlphabetically);
    }

    @PutMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MassageDto> editMassage(
            @PathVariable Long id,
            @ModelAttribute MassageCreateDto request,
            @RequestPart("file") MultipartFile file) throws IOException {
        Admin admin = securityService.getCurrentAdmin();
        MassageForCompany updatedMassage = massageService.editMassage(id, request, file, admin);
        MassageDto msgDto = massageService.getMassageResponse(updatedMassage);
        return ResponseEntity.ok(msgDto);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity removeMassage(@PathVariable Long id) {
        massageService.removeMassage(id);
        return ResponseEntity.ok().build();
    }
}
