package emino.backend.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import emino.backend.dto.optaplanner.OptaAvailability;
import emino.backend.dto.optaplanner.OptaEmployee;
import emino.backend.dto.optaplanner.OptaSchedule;
import emino.backend.dto.optaplanner.OptaShift;
import emino.backend.dto.optaplanner.OptimizedShiftDto;
import emino.backend.models.Employee;
import emino.backend.models.EmployeeAvailability;
import emino.backend.models.Shift;

@Component
public class OptaPlannerMapper {

    public OptaEmployee toOptaPlannerEmployee(Employee employee) {
        Set<String> skills = employee.getMassagesToHandle() != null ? employee.getMassagesToHandle()
                .stream()
                .map(skill -> skill.getMassage().getName())
                .collect(Collectors.toSet())
                : Collections.emptySet();

        return OptaEmployee.builder()
                .id(employee.getId())
                .name(employee.getName() + " " + employee.getSurname())
                .skills(skills)
                .build();
    }

    public OptaShift toOptaPlannerShift(Shift shift) {
        return OptaShift.builder()
                .id(shift.getId())
                .start(shift.getStartTime())
                .end(shift.getEndTime())
                .location(shift.getRoom().getRoomNumber())
                .requiredSkill(shift.getMassage().getMassage().getName())
                .employee(null)
                .build();
    }

    public OptaAvailability toOptaPlannerAvailability(EmployeeAvailability availability) {
        return OptaAvailability.builder()
                .employee(toOptaPlannerEmployee(availability.getEmployee()))
                .date(availability.getDate())
                .availabilityType(availability.getAvailabilityType())
                .build();
    }

    public List<OptimizedShiftDto> toOptaPlannerSchedule(OptaSchedule optimizedSchedule) {

        return optimizedSchedule.getShifts().stream()
                .map(optaShift -> OptimizedShiftDto.builder()
                        .id(optaShift.getId())
                        .startTime(optaShift.getStart())
                        .endTime(optaShift.getEnd())
                        .location(optaShift.getLocation())
                        .requiredSkill(optaShift.getRequiredSkill())
                        .employeeName(optaShift.getEmployee() != null ? optaShift.getEmployee().getName() : "Nieprzypisany")
                        .build())
                .toList();
    }

    public OptimizedShiftDto toOptimizedShiftDto(Shift shift) {
        String employeeName = shift.getAssignedEmployee() != null
                ? shift.getAssignedEmployee().getName() + " " + shift.getAssignedEmployee().getSurname()
                : "Nieprzypisany";

        return OptimizedShiftDto.builder()
                .id(shift.getId())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .location(shift.getRoom().getRoomNumber())
                .requiredSkill(shift.getMassage().getMassage().getName())
                .employeeName(employeeName)
                .build();
    }

    public List<OptimizedShiftDto> toOptimizedShiftDtoList(List<Shift> shifts) {
        return shifts.stream()
                .map(this::toOptimizedShiftDto)
                .toList();
    }
}
