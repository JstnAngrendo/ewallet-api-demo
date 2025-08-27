package com.ewallet.wallet_service.controller;


import com.ewallet.wallet_service.dto.*;
import com.ewallet.wallet_service.dto.request.TopUpRequest;
import com.ewallet.wallet_service.dto.request.TransactionHistoryRequest;
import com.ewallet.wallet_service.dto.request.TransferRequest;
import com.ewallet.wallet_service.dto.request.WithdrawRequest;
import com.ewallet.wallet_service.dto.response.ApiResponse;
import com.ewallet.wallet_service.dto.response.PaginatedResponse;
import com.ewallet.wallet_service.dto.response.TransactionResponse;
import com.ewallet.wallet_service.dto.response.WalletResponse;
import com.ewallet.wallet_service.mapper.TransactionMapper;
import com.ewallet.wallet_service.model.Transaction;
import com.ewallet.wallet_service.model.Wallet;
import com.ewallet.wallet_service.security.UserPrincipal;
import com.ewallet.wallet_service.service.TransactionService;
import com.ewallet.wallet_service.service.WalletService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Wallet Controller", description = "Endpoints for wallet operations (Bearer token)")
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WalletController {
    private final WalletService walletService;
    private final TransactionMapper transactionMapper;
    private final TransactionService transactionService;
    private static final Logger log = LoggerFactory.getLogger(WalletService.class);

    @Operation(summary = "Get my wallet balance and details")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<WalletResponse>> getMyWallet(@AuthenticationPrincipal UserPrincipal principal) {
        WalletResponse walletResponse = walletService.getMyWallet(principal.userId());
        return ResponseEntity.ok(ApiResponse.success("Wallet retrieved successfully", walletResponse));
    }

    @Operation(summary = "Top up my wallet")
    @PostMapping("/topup")
    public ResponseEntity<ApiResponse<Wallet>> topUp(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TopUpRequest request) {
        Wallet updatedWallet = walletService.topUp(principal.userId(), request.getAmount());
        return ResponseEntity.ok(ApiResponse.success("Top up successful", updatedWallet));
    }

    @Operation(summary = "Transfer funds to another user")
    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TransferRequest request) {
        Transaction transaction = walletService.transferToUser(
                principal.userId(),
                request.getToUsername(),
                request.getAmount()
        );
        TransactionResponse response = transactionMapper.toResponse(transaction);
        return ResponseEntity.ok(ApiResponse.success("Transfer successful", response));
    }

    @Operation(summary = "Withdraw funds from my wallet")
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<Wallet>> withdraw(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody WithdrawRequest request) {
        Wallet updatedWallet = walletService.withdraw(principal.userId(), request.getAmount());
        return ResponseEntity.ok(ApiResponse.success("Withdrawal successful", updatedWallet));
    }

    @Operation(summary = "Get my transaction history")
    @PostMapping("/transactions")
    public ResponseEntity<ApiResponse<PaginatedResponse<TransactionResponse>>> getTransactions(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TransactionHistoryRequest request) {
        Pageable pageable = transactionMapper.toPageable(request.getPaginationRequest());
        Page<Transaction> transactionsPage = transactionService.getTransactions(principal.userId(), pageable);
        PaginatedResponse<TransactionResponse> response = transactionMapper.toPaginatedResponse(
                transactionsPage,
                transactionMapper::toResponse
        );
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", response));
    }
}
