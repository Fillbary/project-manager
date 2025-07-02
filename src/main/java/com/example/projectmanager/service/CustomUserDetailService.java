package com.example.projectmanager.service;

import com.example.projectmanager.entity.User;
import com.example.projectmanager.repositorу.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username) // Используйте ваш метод поиска
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
