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
import emino.backend.models.Admin;
import emino.backend.models.Employee;
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
            .id(user.getId())
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

            Long companyId = null;
            if (user instanceof Admin admin) {
                companyId = admin.getCompany().getId();
            } else if (user instanceof Employee employee) {
                companyId = employee.getCompany().getId();
            }
    
            var userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .dateOfBirth(user.getDateOfBirth())
                .email(user.getEmail())
                .role(user.getRole())
                .companyId(companyId)
                .build();
    
            return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(userResponse)
                .build();
        } catch (BadCredentialsException | UsernameNotFoundException ex) {
            throw new BadCredentialsException("Niepoprawny email lub hasło");
        }
    }

    public UserResponse updateProfile(Long id, UpdateProfileRequest request ) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego użytkownika"));

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getSurname() != null) {
            user.setSurname(request.getSurname());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getCurrentPassword() != null && request.getNewPassword() != null) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BadCredentialsException("Niepoprawne aktualne hasło");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
     
        User updatedUser = userRepository.save(user);

        var userResponse = UserResponse.builder()
            .id(user.getId())
            .name(updatedUser.getName())
            .surname(updatedUser.getSurname())
            .dateOfBirth(updatedUser.getDateOfBirth())
            .email(updatedUser.getEmail())
            .role(updatedUser.getRole())
            .build();

        return userResponse;
    }
}
