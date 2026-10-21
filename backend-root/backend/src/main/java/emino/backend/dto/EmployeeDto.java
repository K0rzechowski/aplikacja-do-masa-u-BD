package emino.backend.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
   
    private Long id; 
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private String email;
    private String phoneNumber;
    private Long companyId;
    private String companyName;
    private Long branchOfficeId;
    private String branchOfficeName;
    private List<MassageDto> massages;
}