package com.example.ewallet.auth_service.controller;

import com.example.ewallet.auth_service.dto.ApiResponse;
import com.example.ewallet.auth_service.dto.UserRegisterRequest;
import com.example.ewallet.auth_service.dto.UserResponse;
import com.example.ewallet.auth_service.model.User;
import com.example.ewallet.auth_service.service.UserService;
import com.example.ewallet.auth_service.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User Controller", description = "Endpoints for user registration and login without token")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.ok(
                ApiResponse.success("User registered successfully", response)
        );
    }
}
