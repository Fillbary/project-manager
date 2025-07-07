package com.example.projectmanager.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class TaskRequest {

    @NotBlank(message = "Task name cannot be empty")
    @Size(min = 3, max = 100, message = "Название задачи должно быть от 3 до 100 символов")
    private String name;

    private String description;

    @Future(message = "The deadline must be in the future")
    private LocalDateTime deadline;

    @NotNull(message = "Project ID cannot be empty")
    private Long projectId;

    public @NotBlank(message = "Task name cannot be empty")
    @Size(min = 3, max = 100, message = "Название задачи должно быть от 3 до 100 символов") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Task name cannot be empty")
                        @Size(min = 3, max = 100, message = "Название задачи должно быть от 3 до 100 символов") String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public @Future(message = "The deadline must be in the future") LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(@Future(message = "The deadline must be in the future") LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public @NotNull(message = "Project ID cannot be empty") Long getProjectId() {
        return projectId;
    }

    public void setProjectId(@NotNull(message = "Project ID cannot be empty") Long projectId) {
        this.projectId = projectId;
    }
}
