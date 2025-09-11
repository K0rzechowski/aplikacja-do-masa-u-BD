package emino.backend.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import emino.backend.models.UserRole;
import lombok.Data;

@Data
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private String surname;
    @JsonProperty("date_of_birth") 
    private LocalDate dateOfBirth;
    private UserRole role = UserRole.USER; 
}
