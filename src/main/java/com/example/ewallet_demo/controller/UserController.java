package com.example.ewallet_demo.controller;

import com.example.ewallet_demo.dto.UserRegisterRequest;
import com.example.ewallet_demo.model.User;
import com.example.ewallet_demo.service.UserService;
import com.example.ewallet_demo.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        User newUser = userService.registerUser(request.getUsername(), request.getPassword(), request.getRole());
        return ResponseEntity.ok(newUser);
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

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestParam String username,
            @RequestParam String password
    ) {
        try {
            User user = userService.loginUser(username, password);
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            return ResponseEntity.ok().body("Bearer " + token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
