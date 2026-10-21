package emino.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BranchOfficeDto {

    private Long id;
    private Long companyId;
    private String name;
    private String city;
    private String address;
}
