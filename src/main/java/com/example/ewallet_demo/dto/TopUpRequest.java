package com.example.ewallet_demo.dto;

import jakarta.validation.constraints.Min;

public class TopUpRequest {
    @Min(value = 1000, message = "Minimum top-up is 1000")
    private double amount;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
