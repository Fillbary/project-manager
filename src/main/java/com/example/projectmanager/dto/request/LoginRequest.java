package com.example.projectmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    private String password;

    public @NotBlank(message = "Username is required")
            @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters") String getUsername() {
        return username;
    }

    public void setUsername(
            @NotBlank(message = "Username is required")
            @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters") String username) {
        this.username = username;
    }

    public @NotBlank(message = "Password is required")
            @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters") String getPassword() {
        return password;
    }

    public void setPassword(
            @NotBlank(message = "Password is required")
            @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters") String password) {
        this.password = password;
    }
}
