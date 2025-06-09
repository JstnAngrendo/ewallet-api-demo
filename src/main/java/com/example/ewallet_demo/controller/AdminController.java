package com.example.ewallet_demo.controller;


import com.example.ewallet_demo.dto.*;
import com.example.ewallet_demo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin Controller", description = "Endpoints for admin access only")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "Get admin resource")
    @GetMapping
    public ResponseEntity<ApiResponse<String>> getAdminResult() {
        return ResponseEntity.ok(
                ApiResponse.success("Admin resource accessed successfully", "Access granted to admin resource.")
        );
    }

//    @GetMapping("/users")
//    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponse>>> getAllUsers(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//
//    }

    @Operation(summary = "Get all transactions with pagination")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/transactions")
    public ResponseEntity<ApiResponse<PaginatedResponse<AdminTransactionResponse>>> getAllTransactions(
            @RequestBody PaginationRequest paginationRequest) {

        PaginatedResponse<AdminTransactionResponse> response =
                adminService.getAllTransactions(paginationRequest);

        return ResponseEntity.ok(
                ApiResponse.success("Transactions retrieved successfully", response)
        );
    }

}
