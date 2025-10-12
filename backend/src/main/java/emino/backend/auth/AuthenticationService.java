package emino.backend.auth;

import javax.naming.AuthenticationException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import emino.backend.config.JwtService;
import emino.backend.exception.DuplicateEmailException;
import emino.backend.models.User;
import emino.backend.models.UserRole;
import emino.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
     
    public AuthenticationResponse register(RegisterRequest request) {
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Podany email jest już w użyciu");
        }

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

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws AuthenticationException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            // if a user is authenticated, generate a token and send it back
            var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new UsernameNotFoundException("Nie znaleziono użytkownika o takich danych"));
    
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
        } catch (BadCredentialsException | UsernameNotFoundException ex) {
            throw new BadCredentialsException("Niepoprawny email lub hasło");
        }
    }

    // public AuthenticationResponse updateProfile(RegisterRequest request) {
    //     var user = userRepository.findByEmail(request.getEmail());

    // }

}
