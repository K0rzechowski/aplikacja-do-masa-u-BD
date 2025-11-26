package emino.backend.controllers;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.dto.optaplanner.OptaSchedule;
import emino.backend.services.ScheduleOptimizationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ScheduleController {
    
    private final ScheduleOptimizationService scheduleOptimizationService;

    @PostMapping("/optimize/{companyId}")
    public ResponseEntity<OptaSchedule> optimizeSchedule(
            @PathVariable Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        OptaSchedule solution = scheduleOptimizationService.optimizeSchedule(companyId, startTime, endTime);
        return ResponseEntity.ok(solution);
    }
}
