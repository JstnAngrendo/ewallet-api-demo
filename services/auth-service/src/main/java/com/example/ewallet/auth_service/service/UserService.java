package com.example.ewallet.auth_service.service;



import com.example.ewallet.auth_service.dto.UserRegisterRequest;
import com.example.ewallet.auth_service.dto.UserResponse;
import com.example.ewallet.auth_service.enums.Role;
import com.example.ewallet.auth_service.exception.UserNotFoundException;
import com.example.ewallet.auth_service.exception.UsernameAlreadyExistsException;
import com.example.ewallet.auth_service.model.User;
import com.example.ewallet.auth_service.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    @Transactional
    public UserResponse registerUser(UserRegisterRequest request) {
        validateUsername(request.getUsername());

        User newUser = createUser(request);
        User savedUser = userRepository.saveAndFlush(newUser);
        return toUserResponse(savedUser);
    }

    private void validateUsername(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    throw new UsernameAlreadyExistsException("Username " + username + " already exists");
                });
    }

    private User createUser(UserRegisterRequest request) {
        Role userRole = Role.USER;
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                userRole = Role.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {

            }
        }
        return User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .active(true)
                .build();
    }
    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return user;
    }
}
