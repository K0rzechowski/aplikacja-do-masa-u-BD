package emino.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MassageCreateDto {
    
    private String name;
    private int price;
    // private List<Long> symptomIds;
}