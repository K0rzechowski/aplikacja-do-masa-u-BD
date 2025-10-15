package emino.backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeUpdateDto {
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private String email;
    private String phoneNumber;
}
