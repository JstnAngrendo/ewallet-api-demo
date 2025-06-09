package com.example.ewallet_demo.service;


import com.example.ewallet_demo.dto.UserRegisterRequest;
import com.example.ewallet_demo.dto.UserResponse;
import com.example.ewallet_demo.exception.UsernameAlreadyExistsException;
import com.example.ewallet_demo.model.User;
import com.example.ewallet_demo.model.Wallet;
import com.example.ewallet_demo.repository.UserRepository;
import com.example.ewallet_demo.repository.WalletRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final WalletRepository walletRepository;
//    public User registerUser(String username, String password, String role) {
//        if (userRepository.findByUsername(username).isPresent()) {
//            throw new RuntimeException("Username already exists");
//        }
//
//        User newUser = User.builder()
//                .username(username)
//                .password(passwordEncoder.encode(password))
//                .role(role)
//                .active(true)
//                .build();
//        userRepository.save(newUser);
//
//        Wallet wallet = Wallet.builder()
//                .user(newUser)
//                .balance(0.0)
//                .build();
//        walletRepository.save(wallet);
//
//        return newUser;
//    }
@Transactional
public UserResponse registerUser(UserRegisterRequest request) {
    validateUsername(request.getUsername());

    User newUser = createUser(request);
    userRepository.save(newUser);

    Wallet newWallet = Wallet.builder()
            .user(newUser)
            .balance(0.0)
            .build();
    walletRepository.save(newWallet);

    return toUserResponse(newUser);
}

    private void validateUsername(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    throw new UsernameAlreadyExistsException("Username " + username + " already exists");
                });
    }

    private User createUser(UserRegisterRequest request) {
        return User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole().toUpperCase())
                .active(true)
                .build();
    }

//    private void initializeWallet(User user) {
//        walletRepository.save(
//                Wallet.builder()
//                        .user(user)
//                        .balance(0.0)
//                        .build()
//        );
//    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
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
