package com.ewallet.wallet_service.dto.response;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private UUID id;
    private String type;
    private Double amount;
    private String senderUsername;
    private String receiverUsername;
    private String description;
    private String status;
    private LocalDateTime timestamp;
}
