package emino.backend.dto.optaplanner;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime start;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime end;
    private String location;
    private String requiredSkill;
    private OptaEmployee employee;
}
