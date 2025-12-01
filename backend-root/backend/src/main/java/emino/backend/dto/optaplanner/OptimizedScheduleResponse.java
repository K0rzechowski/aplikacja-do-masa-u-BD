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
public class OptimizedScheduleResponse {
    
    private String message;
    private int shiftsOptimized;
    private String score;
    private List<OptimizedShiftDto> shifts;
}