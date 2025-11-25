package emino.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MassageSymptomDto {
    
    private Long massageId; 
    private String massageName;
    private Long symptomId; 
    private String symptomName;
    private Double effectivenessScore;
    private String imageUrl;
    private String companyName;
}