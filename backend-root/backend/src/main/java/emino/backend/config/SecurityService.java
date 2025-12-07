package emino.backend.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import emino.backend.exception.AccessDeniedException;
import emino.backend.models.Admin;
import emino.backend.models.User;
import emino.backend.repositories.AdminRepository;
import emino.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    public Admin getCurrentAdmin() {
        Authentication authentication;
        authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Brak uwierzytelnienia");
        }

        String email = authentication.getName();
        return adminRepository.findByEmail(email).stream()
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Nie znaleziono danego administratora"));
    }

    public User getCurrentUser() {
        Authentication authentication;
        authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Brak uwierzytelnienia");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new AccessDeniedException("Nie znaleziono danego użytkownika"));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}