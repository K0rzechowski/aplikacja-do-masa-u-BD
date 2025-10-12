package emino.backend.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import emino.backend.exception.AccessDeniedException;
import emino.backend.models.Admin;
import emino.backend.repositories.AdminRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final AdminRepository adminRepository;

    public Admin getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AccessDeniedException("Brak uwierzytelnienia");
        }

        String email = authentication.getName();
        return adminRepository.findByEmail(email).stream()
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Nie znaleziono danego administratora"));
    }
}
