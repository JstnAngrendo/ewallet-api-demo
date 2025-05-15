package com.example.ewallet_demo.controller;


import com.example.ewallet_demo.dto.*;
import com.example.ewallet_demo.mapper.TransactionMapper;
import com.example.ewallet_demo.model.Pagination;
import com.example.ewallet_demo.model.Transaction;
import com.example.ewallet_demo.model.User;
import com.example.ewallet_demo.model.Wallet;
import com.example.ewallet_demo.repository.TransactionRepository;
import com.example.ewallet_demo.repository.UserRepository;
import com.example.ewallet_demo.repository.WalletRepository;
import com.example.ewallet_demo.service.TransactionService;
import com.example.ewallet_demo.service.UserService;
import com.example.ewallet_demo.service.WalletService;
import com.example.ewallet_demo.util.JwtUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Wallet Controller", description = "Endpoints for wallet operations (Bearer token)")
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WalletController {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionService transactionService;

    @Operation(summary = "Get my wallet")
    @GetMapping("/me")
    public ResponseEntity<?> getMyWallet( ) {
//        String token = authHeader.substring(7);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        Optional<Wallet> walletOpt = walletRepository.findByUser(userOpt.get());
        if (walletOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Wallet not found");
        }

        return ResponseEntity.ok(walletOpt.get());
    }

//    @PostMapping("/topup")
//    public ResponseEntity<?> topUp(
//            @RequestHeader("Authorization") String authHeader,
//            @RequestParam double amount
//    ) {
//        try {
//            String token = authHeader.substring(7);
//            String username = jwtUtil.extractUsername(token);
//            Wallet updatedWallet = walletService.topUp(username, amount);
//            return ResponseEntity.ok(updatedWallet);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(404).body(e.getMessage());
//        }
//    }

    @Operation(summary = "Top up wallet")
    @PostMapping("/topup")
    public ResponseEntity<?> topUp(
            @Valid @RequestBody TopUpRequest request
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Wallet updatedWallet = walletService.topUp(username, request.getAmount());
        return ResponseEntity.ok(updatedWallet);
    }

    @Operation(summary = "Transfer money to another user")
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
            @RequestParam String toUsername,
            @RequestParam double amount
    ) {
        try {
            String senderUsername = SecurityContextHolder.getContext().getAuthentication().getName();

            Wallet updatedSenderWallet = walletService.transferToUser(senderUsername, toUsername, amount);
            return ResponseEntity.ok(updatedSenderWallet);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @Operation(summary = "Withdraw money from wallet")
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @RequestParam double amount
    ) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Wallet updatedWallet = walletService.withdraw(username, amount);
            return ResponseEntity.ok(updatedWallet);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/transactions")
    public ResponseEntity<ApiResponse<PaginatedResponse<TransactionResponse>>> getTransactions(
            @Valid @RequestBody TransactionHistoryRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Pageable pageable = transactionMapper.toPageable(request.getPaginationRequest());

        Page<Transaction> transactionsPage = transactionService.getTransactions(username, pageable);

        PaginatedResponse<TransactionResponse> response = transactionMapper.toPaginatedResponse(
                transactionsPage,
                transaction -> transactionMapper.toResponse(transaction)
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
