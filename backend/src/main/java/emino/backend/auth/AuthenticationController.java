package emino.backend.auth;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        AuthenticationResponse authResponse = authService.register(request);

        Cookie jwtCookie = new Cookie("jwt", authResponse.getToken());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setSecure(true);
        jwtCookie.setMaxAge(24 * 60 * 60);
        
        response.addCookie(jwtCookie);

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        AuthenticationResponse authResponse = authService.authenticate(request);

        Cookie jwtCookie = new Cookie("jwt", authResponse.getToken());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setSecure(true);
        jwtCookie.setMaxAge(24 * 60 * 60);
        
        response.addCookie(jwtCookie);

        return ResponseEntity.ok(authResponse);
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

}
