package emino.backend.dto.optaplanner;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptaSchedule {
    
    private OptaScheduleState scheduleState;
    private List<OptaAvailability> availabilities;
    private List<OptaEmployee> employees;
    private List<OptaShift> shifts;
}