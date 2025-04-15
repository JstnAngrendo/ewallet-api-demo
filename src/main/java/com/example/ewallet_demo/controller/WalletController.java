package com.example.ewallet_demo.controller;


import com.example.ewallet_demo.dto.TopUpRequest;
import com.example.ewallet_demo.model.Transaction;
import com.example.ewallet_demo.model.User;
import com.example.ewallet_demo.model.Wallet;
import com.example.ewallet_demo.repository.TransactionRepository;
import com.example.ewallet_demo.repository.UserRepository;
import com.example.ewallet_demo.repository.WalletRepository;
import com.example.ewallet_demo.service.UserService;
import com.example.ewallet_demo.service.WalletService;
import com.example.ewallet_demo.util.JwtUtil;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;
    private final TransactionRepository transactionRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getMyWallet(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

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

    @PostMapping("/topup")
    public ResponseEntity<?> topUp(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody TopUpRequest request
    ) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        Wallet updatedWallet = walletService.topUp(username, request.getAmount());
        return ResponseEntity.ok(updatedWallet);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String toUsername,
            @RequestParam double amount
    ) {
        try {
            String token = authHeader.substring(7);
            String senderUsername = jwtUtil.extractUsername(token);

            Wallet updatedSenderWallet = walletService.transferToUser(senderUsername, toUsername, amount);
            return ResponseEntity.ok(updatedSenderWallet);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam double amount
    ) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            Wallet updatedWallet = walletService.withdraw(username, amount);
            return ResponseEntity.ok(updatedWallet);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        List<Transaction> tx = transactionRepository.findBySenderUsernameOrReceiverUsername(username, username);
        return ResponseEntity.ok(tx);
    }


}
