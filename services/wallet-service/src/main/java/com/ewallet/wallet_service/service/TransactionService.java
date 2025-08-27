package com.ewallet.wallet_service.service;

import com.ewallet.wallet_service.model.Transaction;
import com.ewallet.wallet_service.model.User;
import com.ewallet.wallet_service.repository.TransactionRepository;
import com.ewallet.wallet_service.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public Page<Transaction> getTransactions(UUID userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User representation not found for ID: " + userId));

        return transactionRepository.findBySenderOrReceiver(user, user, pageable);
    }
}
