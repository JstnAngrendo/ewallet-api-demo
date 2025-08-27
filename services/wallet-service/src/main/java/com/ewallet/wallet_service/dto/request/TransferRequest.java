package com.ewallet.wallet_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransferRequest {
    @NotBlank(message = "Receiver username cannot be empty")
    private String toUsername;

    @Min(value = 1, message = "Transfer amount must be greater than 0")
    private double amount;
}
