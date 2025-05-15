package com.example.ewallet_demo.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private Long id;
    private String type;
    private Double amount;
    private String senderUsername;
    private String receiverUsername;
    private LocalDateTime timestamp;
}
