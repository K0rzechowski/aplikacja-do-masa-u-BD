package emino.backend.dto.optaplanner;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptaShift {
    
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private String location;
    private String requiredSkill;
    private OptaEmployee employee;
}
