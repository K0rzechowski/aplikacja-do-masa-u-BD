package emino.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShiftResponseDto {
   
    private Long companyId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String roomNumber;
    private String massageName;
    private String assignedEmployeeName;
}