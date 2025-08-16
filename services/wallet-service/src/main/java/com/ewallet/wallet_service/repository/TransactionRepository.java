package com.ewallet.wallet_service.repository;

import com.ewallet.wallet_service.enums.TransactionType;
import com.ewallet.wallet_service.model.Transaction;
import com.ewallet.wallet_service.model.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findBySenderOrReceiver(User sender, User receiver, Pageable pageable);

    Page<Transaction> findBySenderUsernameOrReceiverUsername(String sender, String receiver, Pageable pageable);
    Page<Transaction> findByType(TransactionType type, Pageable pageable);
}

