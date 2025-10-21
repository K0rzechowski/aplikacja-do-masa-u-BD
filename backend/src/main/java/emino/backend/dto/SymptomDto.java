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
public class SymptomDto {
    
    private Long id; 
    private String name;
    private Long companyId;
    private String companyName;
    private List<String> massageList;
}