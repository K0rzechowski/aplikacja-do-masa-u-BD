package emino.backend.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.config.SecurityService;
import emino.backend.dto.GenerateShiftDto;
import emino.backend.dto.ShiftResponseDto;
import emino.backend.models.BranchOffice;
import emino.backend.models.Shift;
import emino.backend.services.BranchOfficeService;
import emino.backend.services.ShiftService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ShiftController {

    private final ShiftService shiftService;
    private final BranchOfficeService branchOfficeService;
    private final SecurityService securityService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateShifts(@RequestBody GenerateShiftDto request) {
        BranchOffice branchOffice = branchOfficeService.getBranchOfficeForAdmin(request.getBranchOfficeId(), securityService.getCurrentAdmin());

        int count = shiftService.generateShiftsForNextWeek(branchOffice, request.getStartDate());

        return ResponseEntity.ok(Map.of(
            "message", "Poprawnie wygenerowano " + count + " zmian.",
            "shiftsCreated", count
        ));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ShiftResponseDto>> getShiftsByCompany(
            @PathVariable Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        return ResponseEntity.ok(toResponse(shiftService.getShiftsByCompany(companyId, startTime, endTime)));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<ShiftResponseDto>> getShiftsByBranchOffice(
            @PathVariable Long branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        return ResponseEntity.ok(toResponse(shiftService.getShiftsByBranchOffice(branchId, startTime, endTime)));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ShiftResponseDto>> getMyShifts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        Long employeeId = securityService.getCurrentEmployee().getId();
        return ResponseEntity.ok(toResponse(shiftService.getShiftsByEmployee(employeeId, startTime, endTime)));
    }

    @PostMapping("/{id}/signup")
    public ResponseEntity<ShiftResponseDto> signUp(@PathVariable Long id) {
        Shift shift = shiftService.signUp(id, securityService.getCurrentEmployee());
        return ResponseEntity.ok(shiftService.getShiftResponse(shift));
    }

    @DeleteMapping("/{id}/signup")
    public ResponseEntity<ShiftResponseDto> signOff(@PathVariable Long id) {
        Shift shift = shiftService.signOff(id, securityService.getCurrentEmployee());
        return ResponseEntity.ok(shiftService.getShiftResponse(shift));
    }

    private List<ShiftResponseDto> toResponse(List<Shift> shifts) {
        return shifts.stream()
                .map(shiftService::getShiftResponse)
                .toList();
    }
}
