package com.example.ewallet_demo.service;


import com.example.ewallet_demo.enums.TransactionStatus;
import com.example.ewallet_demo.enums.TransactionType;
import com.example.ewallet_demo.model.Transaction;
import com.example.ewallet_demo.model.User;
import com.example.ewallet_demo.model.Wallet;
import com.example.ewallet_demo.repository.TransactionRepository;
import com.example.ewallet_demo.repository.UserRepository;
import com.example.ewallet_demo.repository.WalletRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

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

        return wallet;
    }

}
