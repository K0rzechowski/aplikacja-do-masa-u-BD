package emino.backend.dto.optaplanner;

import com.fasterxml.jackson.annotation.JsonFormat;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private java.time.LocalDate firstDraftDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private java.time.LocalDate lastHistoricDate;

    private String score;
}