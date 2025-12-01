package emino.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.dto.AdminDto;
import emino.backend.exception.RecaptchaException;
import emino.backend.models.Admin;
import emino.backend.services.AdminService;
import emino.backend.services.RecaptchaService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AdminController {
    
    private final AdminService adminService;
    private final RecaptchaService recaptchaService;

    @PostMapping("/create")
    public ResponseEntity<Admin> createAdmin(@RequestBody AdminDto request) throws MessagingException, RecaptchaException {
        if (!recaptchaService.verifyRecaptcha(request.getRecaptchaResponse())) {
            throw new RecaptchaException("Weryfikacja reCAPTCHA nie powiodła się") {};
        }
        Admin admin = adminService.createAdmin(request);
        return ResponseEntity.ok(admin);
    }
}
