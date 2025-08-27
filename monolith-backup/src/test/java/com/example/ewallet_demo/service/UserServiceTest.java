package com.example.ewallet_demo.service;

import com.example.ewallet_demo.dto.UserRegisterRequest;
import com.example.ewallet_demo.dto.UserResponse;
import com.example.ewallet_demo.exception.UsernameAlreadyExistsException;
import com.example.ewallet_demo.model.User;
import com.example.ewallet_demo.model.Wallet;
import com.example.ewallet_demo.repository.UserRepository;
import com.example.ewallet_demo.repository.WalletRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


public class UserServiceTest {

    private UserRepository userRepository;
    private WalletRepository walletRepository;
    private UserService userService;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        System.out.println("Setting up test...");
        userRepository = mock(UserRepository.class);
        walletRepository = mock(WalletRepository.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder, walletRepository);
    }

    @Test
    void registerUser_success() {
        System.out.println("Running registerUser_success...");
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        User mockUser = User.builder()
                .id(1L)
                .username("test")
                .password("encodedPassword")
                .role("USER")
                .active(true)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse response = userService.registerUser(
                UserRegisterRequest.builder()
                        .username("test")
                        .password("password")
                        .role("USER")
                        .build()
        );

        assertEquals("test", response.getUsername());
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
        verify(walletRepository).save(any(Wallet.class));
    }
    @Test
    void registerUser_fail_duplicateUsername() {
        User existingUser = User.builder().username("test").build();
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(existingUser));

        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("test")
                .password("password")
                .role("USER")
                .build();

        UsernameAlreadyExistsException exception = assertThrows(
                UsernameAlreadyExistsException.class,
                () -> userService.registerUser(request)
        );

        assertEquals("Username test already exists", exception.getMessage());

        verify(userRepository, never()).save(any());
        verify(walletRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

//    @Test
//    void loginUser_success() {
//        User savedUser = User.builder()
//                .username("test")
//                .password(new BCryptPasswordEncoder().encode("password"))
//                .build();
//
//        when(userRepository.findByUsername("test")).thenReturn(Optional.of(savedUser));
//
//        User loggedInUser = userService.loginUser("test", "password");
//
//        assertEquals("test", loggedInUser.getUsername());
//    }
//
//    @Test
//    void loginUser_fail_userNotFound() {
//        when(userRepository.findByUsername("notexist")).thenReturn(Optional.empty());
//
//        assertThrows(RuntimeException.class, () ->
//                userService.loginUser("notexist", "password"));
//    }
//
//    @Test
//    void loginUser_fail_invalidPassword() {
//        User user = User.builder()
//                .username("test")
//                .password(new BCryptPasswordEncoder().encode("rightpassword"))
//                .build();
//
//        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
//
//        assertThrows(RuntimeException.class, () ->
//                userService.loginUser("test", "wrongpassword"));
//    }
}
