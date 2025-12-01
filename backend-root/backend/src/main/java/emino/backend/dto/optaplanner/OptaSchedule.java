package emino.backend.dto.optaplanner;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonProperty("availabilityList")
    private List<OptaAvailability> availabilities;

    @JsonProperty("employeeList")
    private List<OptaEmployee> employees;

    @JsonProperty("shiftList")
    private List<OptaShift> shifts;
}