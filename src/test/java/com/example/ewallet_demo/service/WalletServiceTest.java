package com.example.ewallet_demo.service;

import com.example.ewallet_demo.model.User;
import com.example.ewallet_demo.model.Wallet;
import com.example.ewallet_demo.repository.TransactionRepository;
import com.example.ewallet_demo.repository.UserRepository;
import com.example.ewallet_demo.repository.WalletRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class WalletServiceTest {
     private WalletRepository walletRepository;
     private UserRepository userRepository;
     private WalletService walletService;
     private TransactionRepository transactionRepository;

     @BeforeEach
     void setUp() {
          walletRepository = mock(WalletRepository.class);
          userRepository = mock(UserRepository.class);
          transactionRepository = mock(TransactionRepository.class);
          walletService = new WalletService(walletRepository, userRepository, transactionRepository);
     }

     @Test
     void topUp_success() {
          User user = User.builder().username("test").build();
          Wallet wallet = Wallet.builder().balance(100.0).user(user).build();

          when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
          when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
          when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

          Wallet result = walletService.topUp("test", 50.0);

          assertEquals(150.0, result.getBalance());
     }

     @Test
     void topUp_invalidAmount() {
          IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                  walletService.topUp("test", -10.0));

          assertEquals("Amount must be greater than 0", exception.getMessage());
     }

     @Test
     void withdraw_success() {
          User user = User.builder().username("test").build();
          Wallet wallet = Wallet.builder().balance(200.0).user(user).build();

          when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
          when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

          Wallet result = walletService.withdraw("test", 50.0);

          assertEquals(150.0, result.getBalance());
     }

     @Test
     void withdraw_insufficientBalance() {
          User user = User.builder().username("test").build();
          Wallet wallet = Wallet.builder().balance(30.0).user(user).build();

          when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
          when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

          RuntimeException exception = assertThrows(RuntimeException.class, () ->
                  walletService.withdraw("test", 50.0));

          assertEquals("Insufficient balance", exception.getMessage());
     }

     @Test
     void transfer_success() {
          User sender = User.builder().username("alice").build();
          Wallet senderWallet = Wallet.builder().balance(100.0).user(sender).build();

          User receiver = User.builder().username("bob").build();
          Wallet receiverWallet = Wallet.builder().balance(50.0).user(receiver).build();

          when(userRepository.findByUsername("alice")).thenReturn(Optional.of(sender));
          when(userRepository.findByUsername("bob")).thenReturn(Optional.of(receiver));
          when(walletRepository.findByUser(sender)).thenReturn(Optional.of(senderWallet));
          when(walletRepository.findByUser(receiver)).thenReturn(Optional.of(receiverWallet));

          Wallet result = walletService.transferToUser("alice", "bob", 30.0);

          assertEquals(70.0, senderWallet.getBalance());
          assertEquals(80.0, receiverWallet.getBalance());
          assertEquals(70.0, result.getBalance());
     }

     @Test
     void transfer_insufficientBalance() {
          User sender = User.builder().username("alice").build();
          Wallet senderWallet = Wallet.builder().balance(20.0).user(sender).build();

          User receiver = User.builder().username("bob").build();
          Wallet receiverWallet = Wallet.builder().balance(50.0).user(receiver).build();

          when(userRepository.findByUsername("alice")).thenReturn(Optional.of(sender));
          when(userRepository.findByUsername("bob")).thenReturn(Optional.of(receiver));
          when(walletRepository.findByUser(sender)).thenReturn(Optional.of(senderWallet));
          when(walletRepository.findByUser(receiver)).thenReturn(Optional.of(receiverWallet));

          RuntimeException exception = assertThrows(RuntimeException.class, () ->
                  walletService.transferToUser("alice", "bob", 30.0));

          assertEquals("Insufficient balance", exception.getMessage());
     }

}
