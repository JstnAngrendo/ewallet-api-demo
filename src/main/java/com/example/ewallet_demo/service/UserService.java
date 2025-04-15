package com.example.ewallet_demo.service;


import com.example.ewallet_demo.model.User;
import com.example.ewallet_demo.model.Wallet;
import com.example.ewallet_demo.repository.UserRepository;
import com.example.ewallet_demo.repository.WalletRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final WalletRepository walletRepository;
    public User registerUser(String username, String password, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User newUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .active(true)
                .build();
        userRepository.save(newUser);

        Wallet wallet = Wallet.builder()
                .user(newUser)
                .balance(0.0)
                .build();
        walletRepository.save(wallet);

        return newUser;
    }
    public User loginUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }
}
