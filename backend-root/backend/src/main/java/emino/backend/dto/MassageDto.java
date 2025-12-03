package emino.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MassageDto {
    
    private Long id; 
    private String name;
    private int price;
    private Long companyId;
    private String companyName;
    private List<SymptomDto> symptoms;
    private String imageUrl;
}