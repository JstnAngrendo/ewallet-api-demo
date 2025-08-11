package com.example.ewallet.auth_service.dto;


import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String username;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;
}
