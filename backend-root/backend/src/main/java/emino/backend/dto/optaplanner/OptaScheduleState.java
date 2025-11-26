package emino.backend.dto.optaplanner;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptaScheduleState {
    
    private Long tenantId;
    private Integer publishLength;
    private Integer draftLength;
    private LocalDate firstDraftDate;
    private LocalDate lastHistoricDate;
}