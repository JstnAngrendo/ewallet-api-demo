package com.example.ewallet_demo.controller;


import com.example.ewallet_demo.dto.ApiResponse;
import com.example.ewallet_demo.dto.TokenResponse;
import com.example.ewallet_demo.model.User;
import com.example.ewallet_demo.repository.UserRepository;
import com.example.ewallet_demo.util.JwtUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth Controller", description = "Endpoints for user login with Jwt Auth")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Operation(summary = "Login user with auth - receiving Bearer token")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestParam String username, @RequestParam String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("unauthorized", "Authentication failed"));
        }
        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("unauthorized", "Authentication failed"));
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        TokenResponse tokenResponse = new TokenResponse(token, "Bearer", jwtUtil.getExpirationInSeconds());

        return ResponseEntity.ok()
                .body(ApiResponse.success("Authentication successful", tokenResponse));
    }
}

