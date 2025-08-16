package com.ewallet.wallet_service.service;

import com.ewallet.wallet_service.dto.UserRegisteredEvent;
import com.ewallet.wallet_service.model.User;
import com.ewallet.wallet_service.model.Wallet;
import com.ewallet.wallet_service.repository.UserRepository;
import com.ewallet.wallet_service.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceListener {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @KafkaListener(topics = "user-registered", groupId = "wallet_group")
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent for userId: {}", event.getUserId());

        User user = User.builder()
                .id(event.getUserId())
                .username(event.getUsername())
                .build();
        userRepository.save(user);
        log.info("User representation saved for userId: {}", user.getId());

        Wallet newWallet = Wallet.builder()
                .user(user)
                .balance(0.0)
                .build();
        walletRepository.save(newWallet);
        log.info("Wallet created for userId: {}", user.getId());
    }
}
