package com.example.projectmanager.controller;

import com.example.projectmanager.dto.request.RegisterRequest;
import com.example.projectmanager.entity.Role;
import com.example.projectmanager.entity.User;
import com.example.projectmanager.repositorу.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegisterController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public RegisterController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping
    @Transactional
    public String register(
            @ModelAttribute("registerRequest") @Valid RegisterRequest registerRequest,
            RedirectAttributes redirectAttributes) {

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Registration was successful!");
        return "redirect:/login";
    }
}
