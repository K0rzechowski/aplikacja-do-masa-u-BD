package emino.backend.auth;

import java.time.LocalDate;

import emino.backend.models.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private String email;
    private UserRole role;
    private Long companyId;
}
