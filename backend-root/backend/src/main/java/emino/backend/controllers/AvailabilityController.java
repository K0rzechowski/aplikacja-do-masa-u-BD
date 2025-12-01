package emino.backend.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.dto.AvailabilityDto;
import emino.backend.services.AvailabilityService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AvailabilityController {
    
    private final AvailabilityService availabilityService;

    @PostMapping("/employee/{employeeId}")
    public ResponseEntity<AvailabilityDto> setAvailability(@PathVariable Long employeeId, @RequestBody AvailabilityDto request) {
        AvailabilityDto response = availabilityService.setAvailability(request);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AvailabilityDto>> getAvailability(
        @PathVariable Long employeeId,
        @RequestParam LocalDate start,
        @RequestParam LocalDate end) {
        
        List<AvailabilityDto> availabilityList = availabilityService.getAvailability(employeeId, start, end);
        return ResponseEntity.ok(availabilityList);
    }

    @GetMapping("/employee/{employeeId}/{start}")
    public ResponseEntity<AvailabilityDto> getAvailabilityByEmployeeAndDate(
        @PathVariable Long employeeId,
        @PathVariable LocalDate start) {
        
        AvailabilityDto availabilityList = availabilityService.getAvailabilityByEmployeeAndStartDate(employeeId, start);
        return ResponseEntity.ok(availabilityList);
    }

    @PutMapping("/employee/{employeeId}")
    public ResponseEntity<AvailabilityDto> editAvailability(@PathVariable Long employeeId, @RequestBody AvailabilityDto request) {
        AvailabilityDto updated = availabilityService.editAvailability(request);

        return ResponseEntity.ok(updated);
    }
}