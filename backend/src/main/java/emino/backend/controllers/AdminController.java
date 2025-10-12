package emino.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.dto.AdminRequest;
import emino.backend.models.Admin;
import emino.backend.services.AdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AdminController {
    
    private final AdminService adminService;

    @PostMapping("/create")
    public ResponseEntity<Admin> createAdmin(@RequestBody AdminRequest request) {
        Admin admin = adminService.createAdmin(request);
        return ResponseEntity.ok(admin);
    }
}
