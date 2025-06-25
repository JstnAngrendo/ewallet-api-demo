package com.example.ewallet_demo.service;


import com.example.ewallet_demo.dto.WalletResponse;
import com.example.ewallet_demo.enums.TransactionStatus;
import com.example.ewallet_demo.enums.TransactionType;
import com.example.ewallet_demo.model.Transaction;
import com.example.ewallet_demo.model.User;
import com.example.ewallet_demo.model.Wallet;
import com.example.ewallet_demo.repository.TransactionRepository;
import com.example.ewallet_demo.repository.UserRepository;
import com.example.ewallet_demo.repository.WalletRepository;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Logger log = LoggerFactory.getLogger(WalletService.class);


    public WalletResponse getMyWallet(String username) {
        String cacheKey = "wallet:" + username;

        WalletResponse cached = (WalletResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("Cache hit for key: {}", cacheKey);
            return cached;
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        WalletResponse response = WalletResponse.builder()
                .id(wallet.getId())
                .username(user.getUsername())
                .balance(wallet.getBalance())
                .build();

        redisTemplate.opsForValue().set(cacheKey, response, Duration.ofMinutes(5));
        log.info("Wallet cached for key: {}", cacheKey);

        return response;
    }

    public Wallet topUp(String username, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance() + amount);

        transactionRepository.save(
                Transaction.builder()
                        .type(TransactionType.TOPUP)
                        .amount(amount)
                        .sender(user)
                        .receiver(user)
                        .description("Top-up wallet")
                        .status(TransactionStatus.COMPLETED)
                        .build()
        );
        redisTemplate.delete("wallet:" + username);
        log.info("Cache invalidated for key: wallet:{}", username);

        return walletRepository.save(wallet);
    }

    @Transactional
    public Transaction transferToUser(String senderUsername, String receiverUsername, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than 0");
        }

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Wallet senderWallet = walletRepository.findByUser(sender)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        Wallet receiverWallet = walletRepository.findByUser(receiver)
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        if (senderWallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        senderWallet.setBalance(senderWallet.getBalance() - amount);
        receiverWallet.setBalance(receiverWallet.getBalance() + amount);

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        Transaction transaction = transactionRepository.save(
                Transaction.builder()
                        .type(TransactionType.TRANSFER)
                        .amount(amount)
                        .sender(sender)
                        .receiver(receiver)
                        .description("Transfer to " + receiverUsername)
                        .build()
        );
        redisTemplate.delete("wallet:" + senderUsername);
        redisTemplate.delete("wallet:" + receiverUsername);
        log.info("Cache invalidated for key: wallet:{} and wallet:{}", senderUsername, receiverUsername);

        return transaction;
    }

    @Transactional
    public Wallet withdraw(String username, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than 0");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);

        transactionRepository.save(
                Transaction.builder()
                        .type(TransactionType.WITHDRAWAL)
                        .amount(amount)
                        .sender(user)
                        .receiver(user)
                        .description("Withdrawal from wallet")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
        redisTemplate.delete("wallet:" + username);
        log.info("Cache invalidated for key: wallet:{}", username);

        return wallet;
    }

    public WalletResponse toWalletResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .username(wallet.getUser().getUsername())
                .balance(wallet.getBalance())
                .build();
    }
}
