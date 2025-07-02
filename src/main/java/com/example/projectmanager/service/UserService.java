package com.example.projectmanager.service;

import com.example.projectmanager.dto.request.LoginRequest;
import com.example.projectmanager.dto.response.LoginResponse;
import com.example.projectmanager.dto.request.RegisterRequest;
import com.example.projectmanager.dto.response.UserResponse;
import com.example.projectmanager.entity.Role;
import com.example.projectmanager.entity.User;
import com.example.projectmanager.repositorу.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {
        // 1. Проверка совпадения паролей
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("The passwords do not match");
        }

        // 2. Проверка уникальности username и email
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        String encodedPassword = encoder.encode(request.getPassword());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + request.getUsername()));

        if(!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        String jwtToken = jwtService.generateToken(user);

        return new LoginResponse(
                jwtToken,
                user.getUsername(),
                user.getRole()
        );
    }
}
