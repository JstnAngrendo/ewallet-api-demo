package com.ewallet.wallet_service.service;


import com.ewallet.wallet_service.dto.response.WalletResponse;
import com.ewallet.wallet_service.enums.TransactionStatus;
import com.ewallet.wallet_service.enums.TransactionType;
import com.ewallet.wallet_service.model.Transaction;
import com.ewallet.wallet_service.model.User;
import com.ewallet.wallet_service.model.Wallet;
import com.ewallet.wallet_service.repository.TransactionRepository;
import com.ewallet.wallet_service.repository.UserRepository;
import com.ewallet.wallet_service.repository.WalletRepository;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
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

    private String getWalletCacheKey(UUID userId) {
        return "wallet:" + userId.toString();
    }
    public WalletResponse getMyWallet(UUID userId) {
        String cacheKey = getWalletCacheKey(userId);

        // Cache checking
        WalletResponse cachedWallet = (WalletResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cachedWallet != null) {
            log.info("Cache hit for wallet with userId: {}", userId);
            return cachedWallet;
        }

        log.info("Cache miss for wallet with userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User representation not found for ID: " + userId));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user ID: " + userId));

        WalletResponse response = toWalletResponse(wallet);

        redisTemplate.opsForValue().set(cacheKey, response, Duration.ofMinutes(10));
        log.info("Wallet cached for userId: {}", userId);

        return response;
    }

    public Wallet topUp(UUID userId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User representation not found for ID: " + userId));

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
        redisTemplate.delete(getWalletCacheKey(userId));
        log.info("Cache invalidated for wallet with userId: {}", userId);

        return walletRepository.save(wallet);
    }

    @Transactional
    public Transaction transferToUser(UUID senderId, String receiverUsername, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than 0");
        }

        User sender = userRepository.findById(senderId)
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

        redisTemplate.delete(getWalletCacheKey(sender.getId()));
        redisTemplate.delete(getWalletCacheKey(receiver.getId()));
        log.info("Cache invalidated for sender {} and receiver {}", sender.getId(), receiver.getId());

        return transactionRepository.save(
                Transaction.builder()
                        .type(TransactionType.TRANSFER)
                        .amount(amount)
                        .sender(sender)
                        .receiver(receiver)
                        .description("Transfer to " + receiverUsername)
                        .build()
        );
    }
    @Transactional
    public Wallet withdraw(UUID userId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than 0");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User representation not found for ID: " + userId));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance() - amount);


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
        redisTemplate.delete(getWalletCacheKey(userId));
        log.info("Cache invalidated for wallet with userId: {}", userId);

        return walletRepository.save(wallet);
    }

    public WalletResponse toWalletResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .username(wallet.getUser().getUsername())
                .balance(wallet.getBalance())
                .build();
    }
}
