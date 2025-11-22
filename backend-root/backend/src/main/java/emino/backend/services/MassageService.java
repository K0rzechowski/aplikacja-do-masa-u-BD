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
import emino.backend.models.MassageForCompany;
import emino.backend.models.Symptom;
import emino.backend.repositories.CompanyRepository;
import emino.backend.repositories.EmployeeRepository;
import emino.backend.repositories.MassageForCompanyRepository;
import emino.backend.repositories.MassageForSymptomRepository;
import emino.backend.repositories.MassageRepository;
import emino.backend.repositories.SymptomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MassageService {
    
    private final MassageRepository massageRepository;
    private final MassageForCompanyRepository massageCompanyRepository;
    private final SymptomRepository symptomRepository;
    private final EmployeeRepository emplRepository;
    private final CompanyRepository companyRepository;
    private final MassageForSymptomRepository msRepository;
    private final CloudinaryService cloudinaryService;
    
    public List<MassageForCompany> getMassagesByCompany(Long companyId) {
        var company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));
        return massageCompanyRepository.findByCompany(company);
    } 

    public Optional<MassageForCompany> getMassageByName(String name) {
        Optional<Massage> foundMassage = massageRepository.findByName(name);
        if (foundMassage.isPresent()) {
            return massageCompanyRepository.findByMassage(foundMassage.get());
        }
        return Optional.empty();
    } 

    // tu ma zwrócić MassageForCompany
    public List<MassageForCompany> getMassagesBySymptom(Long symptomId) {
        var symptom = symptomRepository.findById(symptomId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego symptomu"));

        List<Massage> foundMassages = massageRepository.findBySymptom(symptom);
        
        return foundMassages.stream()
            .filter(MassageForCompany.class::isInstance)
            .map(MassageForCompany.class::cast)
            .toList();
    } 

    public List<MassageForCompany> getMassagesByEmployee(Long employeeId) {
        Employee employee = emplRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego pracownika"));
        return massageCompanyRepository.findByEmployeeId(employee.getId());
    } 

    @Transactional
    public MassageForCompany createMassage(MassageCreateDto request, Admin admin, MultipartFile file) throws IOException {
        var company = admin.getCompany();

        String imageUrl = cloudinaryService.uploadImage(file, "massages");

        Massage alreadyExistingMassage = massageRepository.findByName(request.getName())
            .orElseGet(() -> {
                Massage massage = new Massage();
                massage.setName(request.getName());
                return massageRepository.save(massage);
            });

        MassageForCompany massageToCreate = MassageForCompany.builder()
            .massage(alreadyExistingMassage)
            .price(request.getPrice())
            .durationInMinutes(request.getMinutes())
            .imageUrl(imageUrl)
            .company(company)
            .build();

        if (!massageToCreate.getCompany().equals(admin.getCompany())) {
            throw new AccessDeniedException("Nie można dodać masażu do nie swojej firmy");
        }

        massageToCreate = massageCompanyRepository.save(massageToCreate); 

        return massageToCreate;
    }

    @Transactional
    public void removeMassage(Long id) {
        var massage = massageCompanyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego masażu"));

        if (massage.getImageUrl() != null) {
            try {
                cloudinaryService.deleteImage(massage.getImageUrl());
            } catch (IOException e) {
                System.err.println("Błąd podczas usuwania obrazu z Cloudinary: " + e.getMessage());
            }
        }

        massageCompanyRepository.deleteById(id);
    }

    @Transactional
    public MassageForCompany editMassage(Long id, MassageCreateDto request, MultipartFile file, Admin admin ) throws IOException {
        MassageForCompany massageForCompany = massageCompanyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego masażu"));

        Massage alreadyExistingMassage = massageRepository.findByName(request.getName())
        .orElseGet(() -> {
            Massage massage = new Massage();
            massage.setName(request.getName());
            return massageRepository.save(massage);
        });

        if (request.getName() != null) {
            massageForCompany.setMassage(alreadyExistingMassage);
        }
        if (request.getPrice() > 0) {
            massageForCompany.setPrice(request.getPrice());
        }
        if (request.getMinutes() > 0) {
            massageForCompany.setDurationInMinutes(request.getMinutes());
        }

        if (!massageForCompany.getCompany().equals(admin.getCompany())) {
            throw new AccessDeniedException("Nie można dodać masażu do nie swojej firmy");
        }

        if (file != null && !file.isEmpty()) {
            if (massageForCompany.getImageUrl() != null) {
                try {
                    cloudinaryService.deleteImage(massageForCompany.getImageUrl());
                } catch (IOException e) {
                    System.err.println("Błąd podczas usuwania obrazu z Cloudinary: " + e.getMessage());
                }
            }
            String newImageUrl = cloudinaryService.uploadImage(file, "massages");
            massageForCompany.setImageUrl(newImageUrl);
        }

        return massageCompanyRepository.save(massageForCompany);
    }

    // private void createMassageSymptomAssociations(Massage massage, List<Long> symptomIds) {
    //     for (Long symptomId : symptomIds) {
    //         Symptom symptom = symptomRepository.findById(symptomId)
    //             .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego symptomu"));

    //         MassageSymptom id = new MassageSymptom();
    //         id.setMassageId(massage.getId());
    //         id.setSymptomId(symptom.getId());

    //         MassageForSymptom massageForSymptom = new MassageForSymptom();
    //         massageForSymptom.setId(id);
    //         massageForSymptom.setMassage(massage);
    //         massageForSymptom.setSymptom(symptom);

    //         msRepository.save(massageForSymptom);
    //     }
    // }

    public MassageDto getMassageResponse(MassageForCompany massage) {
        Massage alreadyExistingMassage = massageRepository.findByName(massage.getMassage().getName()).orElse(null);

        if (alreadyExistingMassage != null) {
            List<Symptom> symptoms = symptomRepository.findByMassage(alreadyExistingMassage);
            
            MassageDto dto = new MassageDto();
            dto.setId(massage.getId());
            dto.setName(alreadyExistingMassage.getName());
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
        return null;
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
