package com.example.ewallet_demo.repository;

import com.example.ewallet_demo.model.User;
import com.example.ewallet_demo.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);
}
