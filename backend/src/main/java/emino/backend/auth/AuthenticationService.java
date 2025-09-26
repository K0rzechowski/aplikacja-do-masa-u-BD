package emino.backend.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import emino.backend.dto.UserResponse;
import emino.backend.models.User;
import emino.backend.models.UserRole;
import emino.backend.repositories.UserRepository;
import emino.backend.services.JwtService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
     
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
            .name(request.getName())
            .surname(request.getSurname())
            .dateOfBirth(request.getDateOfBirth())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(UserRole.USER)
            .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);

        var userResponse = UserResponse.builder()
            .name(request.getName())
            .surname(request.getSurname())
            .dateOfBirth(request.getDateOfBirth())
            .email(request.getEmail())
            .role(user.getRole())
            .build();

        return AuthenticationResponse.builder()
            .token(jwtToken)
            .user(userResponse)
            .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        // if a user is authenticated, generate a token and send it back
        // TODO might have to throw the correct excepttion
        var user = userRepository.findByEmail(request.getEmail())
            .orElseThrow();

        var jwtToken = jwtService.generateToken(user);

        var userResponse = UserResponse.builder()
            .name(user.getName())
            .surname(user.getSurname())
            .dateOfBirth(user.getDateOfBirth())
            .email(user.getEmail())
            .role(user.getRole())
            .build();

        return AuthenticationResponse.builder()
            .token(jwtToken)
            .user(userResponse)
            .build();
    }

}
