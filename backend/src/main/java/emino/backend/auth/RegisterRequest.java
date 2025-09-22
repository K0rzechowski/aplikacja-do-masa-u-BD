package emino.backend.auth;

import org.springframework.cglib.core.Local;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
   
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private String email;
    private String password;
}
