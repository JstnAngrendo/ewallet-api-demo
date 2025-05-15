package com.example.ewallet_demo.repository;

import com.example.ewallet_demo.model.Transaction;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
//    List<Transaction> findBySenderUsernameOrReceiverUsername(String sender, String receiver);

    // Change return type to Page<Transaction> and add Pageable parameter
    Page<Transaction> findBySenderUsernameOrReceiverUsername(String sender, String receiver, Pageable pageable);
}

