package emino.backend.auth;

import java.util.Map;

import javax.naming.AuthenticationException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.exception.DuplicateEmailException;
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

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request, HttpServletResponse response) {
        try {
            AuthenticationResponse authResponse = authService.register(request);

            Cookie jwtCookie = new Cookie("jwt", authResponse.getToken());
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
            Cookie jwtCookie = new Cookie("jwt", authResponse.getToken());
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setSecure(true);
            jwtCookie.setMaxAge(24 * 60 * 60);
            
            response.addCookie(jwtCookie);

            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationException ex) {
            throw ex;
        }        
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map> authenticate(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwt","");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setSecure(true);
        jwtCookie.setMaxAge(0);
        
        response.addCookie(jwtCookie);

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // @PutMapping("/update")
    // public ResponseEntity<AuthenticationResponse> updateProfile(@RequestBody RegisterRequest request, HttpServletResponse response) {
    //     AuthenticationResponse authResponse = authService.(request);

    //     Cookie jwtCookie = new Cookie("jwt", authResponse.getToken());
    //     jwtCookie.setHttpOnly(true);
    //     jwtCookie.setPath("/");
    //     jwtCookie.setSecure(true);
    //     jwtCookie.setMaxAge(24 * 60 * 60);
        
    //     response.addCookie(jwtCookie);

    //     return ResponseEntity.ok(authResponse);
    // }

    // Endpoint to verify JWT token validity
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); 

        if(auth != null && auth.isAuthenticated() && !"anynomousUser".equals(auth.getName())) {
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

    //This is just for now, later the user will get a custom link to reset password
    @PatchMapping("/{userId}/password")
    public ResponseEntity<Map> changePassword(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        authService.changeUserPassword(userId, request.get("newPassword"));
        return ResponseEntity.ok().build();
    }
}
