package com.example.ewallet_demo.dto;

import com.example.ewallet_demo.enums.TransactionType;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminTransactionResponse {
    private Long id;
    private String type;
    private Double amount;
    private String senderUsername;
    private String receiverUsername;
    private String description;
    private String status;
    private LocalDateTime timestamp;
}
