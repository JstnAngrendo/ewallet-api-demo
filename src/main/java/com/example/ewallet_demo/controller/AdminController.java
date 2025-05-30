package com.example.ewallet_demo.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin Controller", description = "Endpoints for admin access only")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    @Operation(summary = "Get admin resource")
    @GetMapping
    public ResponseEntity<String> getAdminResult() {
        return ResponseEntity.ok("Access granted to admin resource.");
    }
}
