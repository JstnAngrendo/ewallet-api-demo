package com.example.ewallet_demo.service;

import com.example.ewallet_demo.model.Transaction;
import com.example.ewallet_demo.repository.TransactionRepository;
import com.example.ewallet_demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public Page<Transaction> getTransactions(String username, Pageable pageable) {
        return transactionRepository.findBySenderUsernameOrReceiverUsername(
                username,
                username,
                pageable
        );
    }
}
