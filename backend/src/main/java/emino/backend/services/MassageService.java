package emino.backend.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import emino.backend.cloudinary.CloudinaryService;
import emino.backend.dto.MassageCreateDto;
import emino.backend.dto.MassageDto;
import emino.backend.dto.SymptomDto;
import emino.backend.exception.AccessDeniedException;
import emino.backend.models.Admin;
import emino.backend.models.Employee;
import emino.backend.models.Massage;
import emino.backend.models.MassageForSymptom;
import emino.backend.models.MassageSymptom;
import emino.backend.models.Symptom;
import emino.backend.repositories.CompanyRepository;
import emino.backend.repositories.EmployeeRepository;
import emino.backend.repositories.MassageForSymptomRepository;
import emino.backend.repositories.MassageRepository;
import emino.backend.repositories.SymptomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MassageService {
    
    private final MassageRepository massageRepository;
    private final SymptomRepository symptomRepository;
    private final EmployeeRepository emplRepository;
    private final CompanyRepository companyRepository;
    private final MassageForSymptomRepository msRepository;
    private final CloudinaryService cloudinaryService;
    
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

    public List<Massage> getMassagesByEmployee(Long employeeId) {
        Employee employee = emplRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego pracownika"));
        return massageRepository.findByEmployeeId(employee.getId());
    } 

    @Transactional
    public Massage createMassage(MassageCreateDto request, Admin admin, MultipartFile file) throws IOException {
        var company = admin.getCompany();

        String imageUrl = cloudinaryService.uploadImage(file, "massages");

        Massage massage = Massage.builder()
            .name(request.getName())
            .price(request.getPrice())
            .durationInMinutes(request.getMinutes())
            .imageUrl(imageUrl)
            .company(company)
            .build();

        if (!massage.getCompany().equals(admin.getCompany())) {
            throw new AccessDeniedException("Nie można dodać masażu do nie swojej firmy");
        }

        massage = massageRepository.save(massage); 

        if (request.getSymptomIds() != null && !request.getSymptomIds().isEmpty()) {
            createMassageSymptomAssociations(massage, request.getSymptomIds());
        }

        return massage;
    }

    @Transactional
    public void removeMassage(Long id) {
        var massage = massageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego masażu"));

        if (massage.getImageUrl() != null) {
            try {
                cloudinaryService.deleteImage(massage.getImageUrl());
            } catch (IOException e) {
                System.err.println("Błąd podczas usuwania obrazu z Cloudinary: " + e.getMessage());
            }
        }
        
        msRepository.deleteByMassageId(id);
        massageRepository.deleteById(id);
    }

    @Transactional
    public Massage editMassage(Long id, MassageCreateDto request, MultipartFile file, Admin admin ) throws IOException {
        Massage massage = massageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego masażu"));

        if (request.getName() != null) {
            massage.setName(request.getName());
        }
        if (request.getPrice() > 0) {
            massage.setPrice(request.getPrice());
        }
        if (request.getMinutes() > 0) {
            massage.setDurationInMinutes(request.getMinutes());
        }

        if (!massage.getCompany().equals(admin.getCompany())) {
            throw new AccessDeniedException("Nie można dodać masażu do nie swojej firmy");
        }

        if (file != null && !file.isEmpty()) {
            if (massage.getImageUrl() != null) {
                try {
                    cloudinaryService.deleteImage(massage.getImageUrl());
                } catch (IOException e) {
                    System.err.println("Błąd podczas usuwania obrazu z Cloudinary: " + e.getMessage());
                }
            }
            String newImageUrl = cloudinaryService.uploadImage(file, "massages");
            massage.setImageUrl(newImageUrl);
        }
     
        if (request.getSymptomIds() != null) {
            msRepository.deleteByMassageId(id);

            if( !request.getSymptomIds().isEmpty()) {
                createMassageSymptomAssociations(massage, request.getSymptomIds());
            }
        }

        return massageRepository.save(massage);
    }

    private void createMassageSymptomAssociations(Massage massage, List<Long> symptomIds) {
        for (Long symptomId : symptomIds) {
            Symptom symptom = symptomRepository.findById(symptomId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego symptomu"));

            MassageSymptom id = new MassageSymptom();
            id.setMassageId(massage.getId());
            id.setSymptomId(symptom.getId());

            MassageForSymptom massageForSymptom = new MassageForSymptom();
            massageForSymptom.setId(id);
            massageForSymptom.setMassage(massage);
            massageForSymptom.setSymptom(symptom);

            msRepository.save(massageForSymptom);
        }
    }

    public MassageDto getMassageResponse(Massage massage) {
        List<Symptom> symptoms = symptomRepository.findByMassage(massage);

        MassageDto dto = new MassageDto();
        dto.setId(massage.getId());
        dto.setName(massage.getName());
        dto.setPrice(massage.getPrice());
        dto.setMinutes(massage.getDurationInMinutes());
        dto.setCompanyId(massage.getCompany().getId());
        dto.setCompanyName(massage.getCompany().getName());
        dto.setSymptoms(
            symptoms.stream()
                .map(s -> new SymptomDto(s.getId(), s.getName()))
                .toList());

        String imageUrl = massage.getImageUrl();
        String publicId = extractPublicIdFromImageUrl(imageUrl);

        dto.setImageUrl(publicId);

        return dto;
    }

    private String extractPublicIdFromImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        String[] parts = imageUrl.split("/upload/");
        if (parts.length > 1) {
            String withoutUpload = parts[1];
            String withoutVersion = withoutUpload.replaceAll("^v\\d+/", "");
            String publicIdWithExtension = withoutVersion.substring(0, withoutVersion.lastIndexOf('.'));

            return publicIdWithExtension;
        }
        return null;
    }
}
