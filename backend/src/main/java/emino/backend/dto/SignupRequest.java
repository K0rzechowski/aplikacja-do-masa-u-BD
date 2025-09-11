package emino.backend.dto;

import emino.backend.models.UserRole;
import lombok.Data;

@Data
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private String surname;
    private UserRole role = UserRole.USER; 
}