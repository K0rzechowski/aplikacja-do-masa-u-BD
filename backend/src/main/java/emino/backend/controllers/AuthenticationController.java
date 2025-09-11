package emino.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.dto.LoginRequest;
import emino.backend.dto.SignupRequest;
import emino.backend.models.User;
import emino.backend.models.UserRole;
import emino.backend.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private  AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    @PostMapping("/login")
public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request){
    
    try {
        // Create authentication token
        Authentication authenticationRequest = 
                UsernamePasswordAuthenticationToken.unauthenticated(
                    loginRequest.getEmail(), 
                    loginRequest.getPassword()
                );
        
        // Authenticate user
        Authentication authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);
        
        // Set security context
        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
        
        // Create/update session
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        
        return new ResponseEntity<>("User signed-in successfully!", HttpStatus.OK);
        
    } catch (BadCredentialsException e) {
        return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
        return new ResponseEntity<>("Login failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest, HttpServletRequest request){
        
        System.out.println("Signup endpoint reached for email: " + signupRequest.getEmail());
        
        if(userRepository.existsByEmail(signupRequest.getEmail())){
            System.out.println("Email already exists: " + signupRequest.getEmail());
            return new ResponseEntity<>("Email already used!", HttpStatus.BAD_REQUEST);
        }

        try {
            User user = new User();
            user.setName(signupRequest.getName());
            user.setSurname(signupRequest.getSurname());
            user.setEmail(signupRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
            
            UserRole role = signupRequest.getRole() != null ? signupRequest.getRole() : UserRole.USER;
            user.setRole(role);

            User savedUser = userRepository.save(user);
            System.out.println("User saved successfully with ID: " + savedUser.getId());
            
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
            
        } catch (Exception e) {
            System.err.println("Error during signup: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Registration failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        logoutHandler.logout(request, response, authentication);

        request.getSession().removeAttribute("SPRING_SECURITY_CONTEXT");
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);


    }
}