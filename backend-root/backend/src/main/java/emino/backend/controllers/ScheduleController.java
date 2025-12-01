package emino.backend.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import emino.backend.dto.optaplanner.OptaAvailability;
import emino.backend.dto.optaplanner.OptaEmployee;
import emino.backend.dto.optaplanner.OptaSchedule;
import emino.backend.dto.optaplanner.OptaShift;
import emino.backend.dto.optaplanner.OptimizedScheduleResponse;
import emino.backend.dto.optaplanner.OptimizedShiftDto;
import emino.backend.mapper.OptaPlannerMapper;
import emino.backend.models.Employee;
import emino.backend.models.EmployeeAvailability;
import emino.backend.models.Shift;
import emino.backend.services.AvailabilityService;
import emino.backend.services.EmployeeService;
import emino.backend.services.ScheduleOptimizationService;
import emino.backend.services.ShiftService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ScheduleController {

        private final ScheduleOptimizationService scheduleOptimizationService;
        private final OptaPlannerMapper optaPlannerMapper;
        private final EmployeeService employeeService;
        private final AvailabilityService availabilityService;
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

        @PostMapping("/optimize/{companyId}")
        public ResponseEntity<OptimizedScheduleResponse> optimizeSchedule(
                        @PathVariable Long companyId,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) throws JsonProcessingException {

                OptaSchedule optaSchedule = prepareOptaSchedule(companyId, startTime, endTime);

                OptaSchedule optimizedSchedule = scheduleOptimizationService.optimizeSchedule(optaSchedule);

                int updatedCount = updateShiftsFromOptimizedSchedule(optimizedSchedule);

                List<OptimizedShiftDto> optimizedShifts = optaPlannerMapper
                        .toOptaPlannerSchedule(optimizedSchedule);

                OptimizedScheduleResponse response = OptimizedScheduleResponse.builder()
                        .message("Poprawnie zoptymalizowano " + updatedCount + " zmian.")
                        .shiftsOptimized(updatedCount)
                        .score(optimizedSchedule.getScheduleState() != null
                                ? optimizedSchedule.getScheduleState().getScore()
                                : "N/A")
                        .shifts(optimizedShifts)
                        .build();

                return ResponseEntity.ok(response);
        }

        private OptaSchedule prepareOptaSchedule(Long companyId, LocalDateTime startTime, LocalDateTime endTime) {
                List<Shift> shifts = shiftService.getShiftsByCompany(companyId, startTime, endTime);
                List<OptaShift> optaShifts = shifts.stream()
                        .map(optaPlannerMapper::toOptaPlannerShift)
                        .toList();

                List<Employee> employees = employeeService.getEmployeesByCompany(companyId);
                List<OptaEmployee> optaEmployees = employees.stream()
                        .map(optaPlannerMapper::toOptaPlannerEmployee)
                        .toList();

                LocalDate startDate = startTime.toLocalDate();
                LocalDate endDate = endTime.toLocalDate();
                List<EmployeeAvailability> availabilities = availabilityService.getAvailabilities(companyId, startDate, endDate);
                List<OptaAvailability> optaAvail = availabilities.stream()
                        .map(optaPlannerMapper::toOptaPlannerAvailability)
                        .toList();

                emino.backend.dto.optaplanner.OptaScheduleState scheduleState = emino.backend.dto.optaplanner.OptaScheduleState
                        .builder()
                        .tenantId(companyId)
                        .publishLength(7)
                        .draftLength(7)
                        .firstDraftDate(startDate)
                        .lastHistoricDate(startDate.minusDays(1))
                        .build();

                return OptaSchedule.builder()
                        .shifts(optaShifts)
                        .employees(optaEmployees)
                        .availabilities(optaAvail)
                        .scheduleState(scheduleState)
                        .build();
        }

        private int updateShiftsFromOptimizedSchedule(OptaSchedule optimizedSchedule) {
                int updatedCount = 0;

                for (OptaShift optaShift : optimizedSchedule.getShifts()) {
                        if (optaShift.getEmployee() != null) {
                                Shift shift = shiftService.getShiftById(optaShift.getId())
                                        .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono zmiany o id: " + optaShift.getId()));
                                Employee employee = employeeService.getEmployeeById(optaShift.getEmployee().getId())
                                        .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pracownika o id: " + optaShift.getEmployee().getId()));

                                shift.setAssignedEmployee(employee);
                                shiftService.saveShift(shift);
                                updatedCount++;
                        }
                }

                return updatedCount;
        }
}