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
import emino.backend.repositories.AdminRepository;
import emino.backend.repositories.EmployeeRepository;
import emino.backend.repositories.UserRepository;
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
            AuthenticationResponse authResponse;

            authResponse = authService.authenticate(request);
            Cookie jwtCookie = new Cookie("jwtToken", authResponse.getToken());
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setSecure(true);
            jwtCookie.setMaxAge(24 * 60 * 60);
            
            response.addCookie(jwtCookie);

            System.out.println("Set jwtToken cookie for user: " + authResponse.getUser().getEmail());
    

            return ResponseEntity.ok(authResponse);
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

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> updateProfile(@PathVariable Long id, @RequestBody UpdateProfileRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            UserResponse updatedUser = authService.updateProfile(id, request);
            return ResponseEntity.ok(updatedUser);
        }

        // If not authenticated, return 403 Forbidden
        return ResponseEntity.status(403).build(); 
    }

    // Endpoint to verify jwtToken token validity
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); 

        if(auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "authenticated", true,
                "user", auth.getName()
            ));
        }

        return ResponseEntity.ok(Map.of(
                "status", "error",
                "authenticated", false,
                "message", "Not authenticated"
            ));
    }
}
