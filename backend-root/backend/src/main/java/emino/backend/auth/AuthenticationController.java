package emino.backend.auth;

import java.util.Map;

import javax.naming.AuthenticationException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.exception.DuplicateEmailException;
import emino.backend.exception.RecaptchaException;
import emino.backend.models.Admin;
import emino.backend.models.Employee;
import emino.backend.repositories.AdminRepository;
import emino.backend.repositories.EmployeeRepository;
import emino.backend.repositories.UserRepository;
import emino.backend.services.RecaptchaService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthenticationController {
    
    private final AuthenticationService authService;
    private final AdminRepository adminRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RecaptchaService recaptchaService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request, HttpServletResponse response) {
        try {
            AuthenticationResponse authResponse = authService.register(request);

            Cookie jwtCookie = new Cookie("jwtToken", authResponse.getToken());
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setSecure(true);
            jwtCookie.setMaxAge(24 * 60 * 60);
            
            response.addCookie(jwtCookie);

            return ResponseEntity.ok(authResponse);
        } catch (DuplicateEmailException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            if (!recaptchaService.verifyRecaptcha(request.getRecaptchaResponse())) {
                throw new RecaptchaException("Weryfikacja reCAPTCHA nie powiodła się") {};
            }

            AuthenticationResponse authResponse;

            authResponse = authService.authenticate(request);
            Cookie jwtCookie = new Cookie("jwtToken", authResponse.getToken());
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setSecure(true);
            jwtCookie.setMaxAge(24 * 60 * 60);
            
            response.addCookie(jwtCookie);

            return ResponseEntity.ok(authResponse);
        } catch (RecaptchaException ex) {
            throw ex;
        } catch (AuthenticationException ex) {
            throw ex;
        }        
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map> authenticate(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwtToken","");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setSecure(true);
        jwtCookie.setMaxAge(0);
        
        response.addCookie(jwtCookie);

        return ResponseEntity.ok(Map.of("message", "Poprawnie wylogowano"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> updateProfile(@PathVariable Long id, @RequestBody UpdateProfileRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            UserResponse updatedUser = authService.updateProfile(id, request);
            return ResponseEntity.ok(updatedUser);
        }

        return ResponseEntity.status(403).build(); 
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); 

        if(auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {

            String email = auth.getName();

            var user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "authenticated", false,
                    "message", "Nie znaleziono użytkownika"
                ));
            }

            var userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .dateOfBirth(user.getDateOfBirth())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

            if (user instanceof Admin admin) {
                userResponse.setCompanyId(admin.getCompany().getId());
            } else if (user instanceof Employee employee) {
                userResponse.setCompanyId(employee.getCompany().getId());
            }

            return ResponseEntity.ok(Map.of(
                "status", "success",
                "authenticated", true,
                "user", userResponse
            ));
        }

        return ResponseEntity.ok(Map.of(
                "status", "error",
                "authenticated", false,
                "message", "Not authenticated"
            ));
    }
}
