package emino.backend.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.dto.GenerateShiftDto;
import emino.backend.dto.ShiftResponseDto;
import emino.backend.models.Shift;
import emino.backend.services.CompanyService;
import emino.backend.services.ShiftService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ShiftController {
    
    private final ShiftService shiftService;
    private final CompanyService companyService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateShifts(@RequestBody GenerateShiftDto request) {
        var company = companyService.getCompanyById(request.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono firmy o danym id"));

        int count = shiftService.generateShiftsForNextWeek(company, request.getStartDate());

        return ResponseEntity.ok(Map.of(
            "message", "Poprawnie wygenerowano " + count + " zmian.",
            "shiftsCreated", count
        ));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ShiftResponseDto>> optimizeSchedule(
            @PathVariable Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        List<Shift> shifts = shiftService.getShiftsByCompany(companyId, startTime, endTime);

        List<ShiftResponseDto> dto = shifts.stream()
                .map(shiftService::getShiftResponse)
                .toList();

        return ResponseEntity.ok(dto);
    }

}
