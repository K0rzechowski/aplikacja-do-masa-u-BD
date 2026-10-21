package emino.backend.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import emino.backend.dto.optaplanner.OptimizedShiftDto;
import emino.backend.models.Shift;

@Component
public class OptaPlannerMapper {

        public OptimizedShiftDto toOptimizedShiftDto(Shift shift) {
                String employeeName = shift.getAssignedEmployee() != null
                        ? shift.getAssignedEmployee().getName() + " " + shift.getAssignedEmployee().getSurname()
                        : "Nieprzypisany";

                return OptimizedShiftDto.builder()
                        .id(shift.getId())
                        .startTime(shift.getStartTime())
                        .endTime(shift.getEndTime())
                        .location(shift.getRoom().getRoomNumber())
                        .employeeName(employeeName)
                        .build();
        }

        public List<OptimizedShiftDto> toOptimizedShiftDtoList(List<Shift> shifts) {
                return shifts.stream()
                        .map(this::toOptimizedShiftDto)
                        .toList();
        }
}
