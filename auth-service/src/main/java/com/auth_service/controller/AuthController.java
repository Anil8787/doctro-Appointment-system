package com.auth_service.controller;

import com.auth_service.dto.ChangePasswordRequestDto;
import com.auth_service.dto.LoginRequest;
import com.auth_service.dto.LoginResponse;
import com.auth_service.dto.RegisterRequest;
import com.auth_service.entity.User;
import com.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
//    public AuthController(AuthService authService) {
//        this.authService = authService;
//    }
    //http://localhost:8085/api/v1/auth/register
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

        @PostMapping("/login")
        public LoginResponse login(@RequestBody LoginRequest request) {
            return authService.login(request);
        }
        @GetMapping("/get-user")
        public User getUser(@RequestParam String email) {
            return authService.findUser(email);
        }

    // Password change endpoint for any logged-in user
    @PostMapping("/change-password")
    @PreAuthorize("hasAnyRole('DOCTOR','PATIENT')")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequestDto dto,
            Authentication authentication
    ) {
        String email = authentication.getName(); // Get user email from JWT
        String result = authService.changePassword(email, dto.getOldPassword(), dto.getNewPassword());
        return ResponseEntity.ok(result);
    }

}
