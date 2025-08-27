package com.example.ewallet.auth_service.controller;


import com.example.ewallet.auth_service.dto.ApiResponse;
import com.example.ewallet.auth_service.dto.LoginRequest;
import com.example.ewallet.auth_service.dto.TokenResponse;
import com.example.ewallet.auth_service.model.User;
import com.example.ewallet.auth_service.service.UserService;
import com.example.ewallet.auth_service.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth Controller", description = "Endpoints for user login with Jwt Auth")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Operation(summary = "Login user with auth - receiving Bearer token")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest loginRequest) {
        User authenticatedUser = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        String token = jwtUtil.generateToken(authenticatedUser.getId(), authenticatedUser.getUsername(), authenticatedUser.getRole().name());
        TokenResponse tokenResponse = new TokenResponse(token, "Bearer", jwtUtil.getExpirationInSeconds());

        return ResponseEntity.ok()
                .body(ApiResponse.success("Authentication successful", tokenResponse));
    }
}

