package emino.backend.mapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import emino.backend.dto.optaplanner.OptaAvailability;
import emino.backend.dto.optaplanner.OptaEmployee;
import emino.backend.dto.optaplanner.OptaSchedule;
import emino.backend.dto.optaplanner.OptaScheduleState;
import emino.backend.dto.optaplanner.OptaShift;
import emino.backend.models.AvailabilityType;
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
            .name(employee.getName() + " " + employee.getSurname())
            .skills(skills)
            .build();
    }

    public OptaShift toOptaPlannerShift(Shift shift) {
        OptaEmployee optaEmployee = null;
        if (shift.getAssignedEmployee() != null) {
            optaEmployee = toOptaPlannerEmployee(shift.getAssignedEmployee());
        }

        return OptaShift.builder()
            .id(shift.getId())
            .start(shift.getStartTime())
            .end(shift.getEndTime())
            .location(shift.getRoom() != null ? shift.getRoom().getRoomNumber() : null)
            .requiredSkill(shift.getMassage().getMassage().getName())
            .employee(optaEmployee)
            .build();
    }

    public OptaAvailability toOptaPlannerAvailability(EmployeeAvailability availability) {
        return OptaAvailability.builder()
            .employee(toOptaPlannerEmployee(availability.getEmployee()))
            .date(availability.getDate())
            .availabilityType(availability.getAvailabilityType())
            .build();
    }

    public OptaScheduleState toOptaPlannerScheduleState(Long companyId, LocalDate startDate) {
        return OptaScheduleState.builder()
            .tenantId(companyId)
            .publishLength(7)
            .draftLength(14)
            .firstDraftDate(startDate.plusDays(7))
            .lastHistoricDate(startDate.minusDays(1))
            .build();
    }

    public OptaSchedule toOptaPlannerSchedule(
        Long companyId,
        List<Employee> employees,
        List<Shift> shifts,
        List<EmployeeAvailability> availabilities,
        LocalDate startDate,
        LocalDate endDate) {
     
        List<OptaEmployee> optaEmployees = employees.stream()
            .map(this::toOptaPlannerEmployee)
            .collect(Collectors.toList());
        
        List<OptaShift> optaShifts = shifts.stream()
            .map(this::toOptaPlannerShift)
            .collect(Collectors.toList());
        
        List<OptaAvailability> optaAvailabilities = new ArrayList<>();
        for (Employee employee : employees ) {
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                final LocalDate date = currentDate;

                AvailabilityType type = availabilities.stream()
                    .filter(avail -> avail.getEmployee().getId().equals(employee.getId()) && avail.getDate().isEqual(date))
                    .findFirst()
                    .map(EmployeeAvailability::getAvailabilityType)
                    .orElse(AvailabilityType.AVAILABLE);

                EmployeeAvailability availability = new EmployeeAvailability();
                availability.setEmployee(employee);
                availability.setDate(currentDate);
                availability.setAvailabilityType(type);

                optaAvailabilities.add(toOptaPlannerAvailability(availability));
                currentDate = currentDate.plusDays(1);
            }
        }

        return OptaSchedule.builder()
            .scheduleState(toOptaPlannerScheduleState(companyId, startDate))
            .employees(optaEmployees)
            .shifts(optaShifts)
            .availabilities(optaAvailabilities)
            .build();
    }
}
