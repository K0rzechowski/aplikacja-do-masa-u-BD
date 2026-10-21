package emino.backend.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.dto.optaplanner.OptimizedScheduleResponse;
import emino.backend.dto.optaplanner.OptimizedShiftDto;
import emino.backend.mapper.OptaPlannerMapper;
import emino.backend.models.Shift;
import emino.backend.services.ShiftService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ScheduleController {

        private final OptaPlannerMapper optaPlannerMapper;
        private final ShiftService shiftService;

        @GetMapping("/company/{companyId}")
        public ResponseEntity<OptimizedScheduleResponse> getOptimizedSchedule(
                        @PathVariable Long companyId,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

                List<Shift> shifts = shiftService.getShiftsByCompany(companyId, startTime, endTime);
                List<OptimizedShiftDto> optimizedShifts = optaPlannerMapper.toOptimizedShiftDtoList(shifts);

                OptimizedScheduleResponse response = OptimizedScheduleResponse.builder()
                        .message("Pobrano harmonogram")
                        .shiftsOptimized(optimizedShifts.size())
                        .score("N/A")
                        .shifts(optimizedShifts)
                        .build();

                return ResponseEntity.ok(response);
        }
}
