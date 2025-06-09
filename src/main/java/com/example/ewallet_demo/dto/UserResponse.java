package com.example.ewallet_demo.dto;


import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;
}
