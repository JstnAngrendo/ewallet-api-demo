package com.ewallet.wallet_service.dto.response;


import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse implements Serializable  {
    private UUID id;
    private String username;
    private double balance;
}
