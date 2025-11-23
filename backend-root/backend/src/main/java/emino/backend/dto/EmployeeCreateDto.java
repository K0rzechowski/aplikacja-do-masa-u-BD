package emino.backend.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCreateDto {
    
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private String email;
    private String phoneNumber;
    private List<Long> massageIds;
}
