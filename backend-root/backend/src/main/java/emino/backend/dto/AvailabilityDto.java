package emino.backend.dto;

import java.time.LocalDate;

import emino.backend.models.AvailabilityType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvailabilityDto {
    
    private Long id;
    private Long employeeId;
    private LocalDate date;
    private AvailabilityType availabilityType;
}