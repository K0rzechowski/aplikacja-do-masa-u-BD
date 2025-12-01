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

    @com.fasterxml.jackson.annotation.JsonProperty("availabilityList")
    private List<OptaAvailability> availabilities;

    @com.fasterxml.jackson.annotation.JsonProperty("employeeList")
    private List<OptaEmployee> employees;

    @com.fasterxml.jackson.annotation.JsonProperty("shiftList")
    private List<OptaShift> shifts;
}