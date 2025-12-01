package emino.backend.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import emino.backend.dto.CompanyDto;
import emino.backend.dto.CompanyOpenTimeDto;
import emino.backend.dto.RoomDto;
import emino.backend.exception.DuplicateCompanyException;
import emino.backend.models.Company;
import emino.backend.models.CompanyOpenTime;
import emino.backend.models.Room;
import emino.backend.repositories.CompanyOpenTimeRepository;
import emino.backend.repositories.CompanyRepository;
import emino.backend.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
    
    private final CompanyRepository companyRepository;
    private final CompanyOpenTimeRepository openTimeRepository;
    private final RoomRepository roomRepository;

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

    public Optional<Company> getByNameAndCity(String name, String city) {
        return companyRepository.findByNameAndCity(name, city);
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

    public void removeCompany(Long id) {
        companyRepository.deleteById(id);
    }

    public CompanyOpenTime setCompanyOpenTime(CompanyOpenTimeDto request) {
        Company company = companyRepository.findById(request.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));

        CompanyOpenTime openTime = CompanyOpenTime.builder()
            .id(request.getId())
            .company(company)
            .openTime(request.getOpenTime())
            .closeTime(request.getCloseTime())
            .build();

        return openTimeRepository.save(openTime);
    }

    public Optional<CompanyOpenTime> getCompanyOpenTime(Long companyId) {
        return openTimeRepository.findByCompanyId(companyId);
    }

    public CompanyOpenTime editCompanyOpenTime(CompanyOpenTimeDto request) {
        Company company = companyRepository.findById(request.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));

        CompanyOpenTime openTime = CompanyOpenTime.builder()
            .id(request.getId())
            .company(company)
            .openTime(request.getOpenTime())
            .closeTime(request.getCloseTime())
            .build();

        return openTimeRepository.save(openTime);
    }

    public Room createRoom(RoomDto request) {
        Company company = companyRepository.findById(request.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));
            
        Room room = Room.builder()
            .id(request.getId())
            .company(company)
            .roomNumber(request.getRoomNumber())
            .build();

        return roomRepository.save(room);
    }

    public Optional<Room> getRoomByCompanyAndRoomNumber(Long companyId, String roomNumber) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));
        
        return roomRepository.findByCompanyAndRoomNumber(company, roomNumber);
    }

    public List<Room> getRoomsByCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));
        
        return roomRepository.findByCompany(company);
    }

    public void editRoom(Long companyId, RoomDto request) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));
            
        Room room = Room.builder()
            .id(request.getId())
            .company(company)
            .roomNumber(request.getRoomNumber())
            .build();

        roomRepository.save(room);
    }

    public void removeRoom(Long id) {
        roomRepository.deleteById(id);
    }
}
