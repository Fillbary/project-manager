package com.example.projectmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // Автоматически возвращает 409 Conflict
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}