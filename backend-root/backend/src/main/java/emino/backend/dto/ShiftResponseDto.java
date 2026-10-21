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
   
    private Long id;
    private Long companyId;
    private Long branchOfficeId;
    private String branchOfficeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String roomNumber;
    private Long assignedEmployeeId;
    private String assignedEmployeeName;
}