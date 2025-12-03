package emino.backend.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import emino.backend.dto.AvailabilityDto;
import emino.backend.models.Employee;
import emino.backend.models.EmployeeAvailability;
import emino.backend.repositories.EmployeeAvailabilityRepository;
import emino.backend.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final EmployeeAvailabilityRepository availabilityRepository;
    private final EmployeeRepository employeeRepository;

    public AvailabilityDto setAvailability(AvailabilityDto request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego pracownika"));

        EmployeeAvailability availability = EmployeeAvailability.builder()
            .employee(employee)
            .date(request.getDate())
            .availabilityType(request.getAvailabilityType())
            .build();

        availability = availabilityRepository.save(availability);
        return mapToDto(availability);
    }

    public List<EmployeeAvailability> getAvailabilities(Long employeeId, LocalDate start, LocalDate end) {
        return availabilityRepository.findByEmployeeIdAndDateBetween(employeeId, start, end);
    }

    public List<EmployeeAvailability> getAvailabilitiesByCompany(Long companyId, LocalDate start, LocalDate end) {
        return availabilityRepository.findByEmployeeCompanyIdAndDateBetween(companyId, start, end);
    }

    public List<AvailabilityDto> getAvailability(Long employeeId, LocalDate start, LocalDate end) {
        return availabilityRepository.findByEmployeeIdAndDateBetween(employeeId, start, end)
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    public AvailabilityDto getAvailabilityByEmployeeAndStartDate(Long employeeId, LocalDate start) {
        EmployeeAvailability availability = availabilityRepository.findByEmployeeIdAndDate(employeeId, start)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej dostępności"));

        return mapToDto(availability);
    }

    public AvailabilityDto editAvailability(AvailabilityDto request) {
        EmployeeAvailability availability = availabilityRepository.findById(request.getId())
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej dostępności"));

        if (request.getAvailabilityType() != null) {
            availability.setAvailabilityType(request.getAvailabilityType());
        }

        availability = availabilityRepository.save(availability);

        return mapToDto(availability);
    }

    private AvailabilityDto mapToDto(EmployeeAvailability availability) {
        return AvailabilityDto.builder()
            .id(availability.getId())
            .employeeId(availability.getEmployee().getId())
            .date(availability.getDate())
            .availabilityType(availability.getAvailabilityType())
            .build();
    }
}