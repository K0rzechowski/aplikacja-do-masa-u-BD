package emino.backend.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyOpenTimeDto {

    private Long id; 
    private Long branchOfficeId;
    private LocalTime openTime;
    private LocalTime closeTime;  
}
