package emino.backend.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.config.SecurityService;
import emino.backend.dto.CompanyDto;
import emino.backend.dto.CompanyOpenTimeDto;
import emino.backend.dto.RoomDto;
import emino.backend.models.Admin;
import emino.backend.models.Company;
import emino.backend.models.CompanyOpenTime;
import emino.backend.models.Room;
import emino.backend.services.CompanyService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class CompanyController {
    
    private final CompanyService companyService;
    private final SecurityService securityService;

    @PostMapping("/create")
    public ResponseEntity<Company> createCompany(@RequestBody CompanyDto request) {
        Company Company = companyService.createCompany(request);
        return ResponseEntity.ok(Company);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<CompanyDto>> getAllCompanies () {
        List<Company> companies = companyService.getAll();
        List<CompanyDto> companyRequests = companies
            .stream()
            .map(company -> new CompanyDto(company.getId(), company.getName(), company.getCity()))
            .toList();
        return ResponseEntity.ok(companyRequests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDto> getById(@PathVariable Long id) {
        Optional<Company> company = companyService.getCompanyById(id);
        if (company.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        CompanyDto dto = new CompanyDto(company.get().getId(), company.get().getName(), company.get().getCity());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity removeCompany(@PathVariable Long id) {
        companyService.removeCompany(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/city/{cityName}")
    public ResponseEntity<List<CompanyDto>> getCompaniesByCity(@PathVariable String cityName) {
        List<Company> companies = companyService.getCompaniesByCity(cityName);
        List<CompanyDto> companyRequests = companies
            .stream()
            .map(company -> new CompanyDto(company.getId(), company.getName(), company.getCity()))
            .toList();
        return ResponseEntity.ok(companyRequests);
    }

    @GetMapping("/{city}/{name}")
    public ResponseEntity<CompanyDto> getCompaniesByNameAndCity(@PathVariable String name, @PathVariable String city) {
        Optional<Company> company = companyService.getByNameAndCity(name, city);
        if (company.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        CompanyDto dto = new CompanyDto(company.get().getId(), company.get().getName(), company.get().getCity());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{companyId}/opentime")
    public ResponseEntity<CompanyOpenTimeDto> setCompanyOpenTime(@PathVariable Long companyId, @RequestBody CompanyOpenTimeDto request) {
        Admin admin = securityService.getCurrentAdmin();
        if (!admin.getCompany().getId().equals(companyId)) {
            return ResponseEntity.status(403).build();
        }
        CompanyOpenTime response = companyService.setCompanyOpenTime(request);
        CompanyOpenTimeDto dto = CompanyOpenTimeDto.builder()
            .id(response.getId())
            .companyId(response.getCompany().getId())
            .openTime(response.getOpenTime())
            .closeTime(response.getCloseTime())
            .build();
        
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{companyId}/openTime")
    public ResponseEntity<CompanyOpenTimeDto> setCompanyOpenTime(@PathVariable Long companyId) {
        Optional<CompanyOpenTime> open = companyService.getCompanyOpenTime(companyId);
        
        if (open != null) {
            CompanyOpenTimeDto response = CompanyOpenTimeDto.builder()
                .id(open.get().getId())
                .companyId(open.get().getCompany().getId())
                .openTime(open.get().getOpenTime())
                .closeTime(open.get().getCloseTime())
                .build();
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{companyId}/room")
    public ResponseEntity<RoomDto> createRoom(@PathVariable Long companyId, @RequestBody RoomDto request) {
        Admin admin = securityService.getCurrentAdmin();
        if (!admin.getCompany().getId().equals(companyId)) {
            return ResponseEntity.status(403).build();
        }
        
        Room room = companyService.createRoom(request);
        RoomDto dto = RoomDto.builder()
            .id(room.getId())
            .companyId(companyId)
            .roomNumber(request.getRoomNumber())
            .build();
        
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{companyId}/{roomNumber}")
    public ResponseEntity<RoomDto> getRoomByCompanyAndRoomNumber(@PathVariable Long companyId, @PathVariable String roomNumber) {
        Optional<Room> room = companyService.getRoomByCompanyAndRoomNumber(companyId, roomNumber);

        if (room != null) {
            RoomDto response =  RoomDto.builder()
            .id(room.get().getId())
            .companyId(companyId)
            .roomNumber(roomNumber)
            .build();
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }
}
