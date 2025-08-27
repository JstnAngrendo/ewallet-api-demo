package com.example.ewallet_demo.repository;

import com.example.ewallet_demo.enums.TransactionType;
import com.example.ewallet_demo.model.Transaction;
import com.example.ewallet_demo.model.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
//    List<Transaction> findBySenderUsernameOrReceiverUsername(String sender, String receiver);

    Page<Transaction> findBySenderOrReceiver(User sender, User receiver, Pageable pageable);

    Page<Transaction> findBySenderUsernameOrReceiverUsername(String sender, String receiver, Pageable pageable);
    Page<Transaction> findByType(TransactionType type, Pageable pageable);
}

