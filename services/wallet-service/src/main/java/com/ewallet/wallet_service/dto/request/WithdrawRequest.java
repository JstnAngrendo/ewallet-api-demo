package com.ewallet.wallet_service.dto.request;


import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class WithdrawRequest {
    @Min(value = 1, message = "Withdrawal amount must be greater than 0")
    private double amount;
}
