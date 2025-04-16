package com.example.ewallet_demo.service;

import com.example.ewallet_demo.model.User;
import com.example.ewallet_demo.model.Wallet;
import com.example.ewallet_demo.repository.UserRepository;
import com.example.ewallet_demo.repository.WalletRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


public class UserServiceTest {
    private UserRepository userRepository;
    private WalletRepository walletRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        walletRepository = mock(WalletRepository.class);
        userService = new UserService(userRepository, walletRepository);
    }

    @Test
    void registerUser_success() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());

        User user = userService.registerUser("test", "password", "USER");

        assertEquals("test", user.getUsername());
        assertTrue(user.isActive());

        verify(userRepository).save(any(User.class));
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void registerUser_fail_duplicateUsername() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.registerUser("test", "password", "USER"));

        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void loginUser_success() {
        User savedUser = User.builder()
                .username("test")
                .password(new BCryptPasswordEncoder().encode("password"))
                .build();

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(savedUser));

        User loggedInUser = userService.loginUser("test", "password");

        assertEquals("test", loggedInUser.getUsername());
    }

    @Test
    void loginUser_fail_userNotFound() {
        when(userRepository.findByUsername("notexist")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                userService.loginUser("notexist", "password"));
    }

    @Test
    void loginUser_fail_invalidPassword() {
        User user = User.builder()
                .username("test")
                .password(new BCryptPasswordEncoder().encode("rightpassword"))
                .build();

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () ->
                userService.loginUser("test", "wrongpassword"));
    }
}
