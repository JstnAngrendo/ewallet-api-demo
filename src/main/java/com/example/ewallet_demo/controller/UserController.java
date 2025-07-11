package com.example.ewallet_demo.controller;

import com.example.ewallet_demo.dto.ApiResponse;
import com.example.ewallet_demo.dto.UserRegisterRequest;
import com.example.ewallet_demo.dto.UserResponse;
import com.example.ewallet_demo.model.User;
import com.example.ewallet_demo.service.UserService;
import com.example.ewallet_demo.util.JwtUtil;
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

//    @PostMapping("/register")
//    public ResponseEntity<User> registerUser(
//            @RequestParam String username,
//            @RequestParam String password,
//            @RequestParam(defaultValue = "USER") String role
//    ) {
//        try {
//            User newUser = userService.registerUser(username, password, role);
//            return ResponseEntity.ok(newUser);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }
//    @Operation(summary = "Login user without auth")
//    @PostMapping("/login")
//    public ResponseEntity<?> loginUser(
//            @RequestParam String username,
//            @RequestParam String password
//    ) {
//        try {
//            User user = userService.loginUser(username, password);
//            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
//            return ResponseEntity.ok().body("Bearer " + token);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(401).body(e.getMessage());
//        }
//    }
}
