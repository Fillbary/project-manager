package com.example.projectmanager.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    @AssertTrue(message = "Passwords don't match")
    public boolean isPasswordMatch() {
        return password != null && password.equals(confirmPassword);
    }

    public
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    String getUsername() {
        return username;
    }

    public void setUsername(
            @NotBlank(message = "Username is required")
            @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
            String username) {
        this.username = username;
    }

    public
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String getEmail() {
        return email;
    }

    public void setEmail(
            @NotBlank(message = "Email is required")
            @Email(message = "Email should be valid")
            String email) {
        this.email = email;
    }

    public
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    String getPassword() {
        return password;
    }

    public void setPassword(
            @NotBlank(message = "Password is required")
            @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
            String password) {
        this.password = password;
    }

    public
    @NotBlank(message = "Please confirm your password")
    String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(
            @NotBlank(message = "Please confirm your password")
            String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
