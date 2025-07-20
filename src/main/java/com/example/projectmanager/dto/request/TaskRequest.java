package com.example.projectmanager.dto.request;

import com.example.projectmanager.entity.Project;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class TaskRequest {
    private Long id;

    @NotBlank(message = "Task name cannot be empty")
    @Size(min = 3, max = 100, message = "Название задачи должно быть от 3 до 100 символов")
    private String name;

    private String description;

    @Future(message = "The deadline must be in the future")
    private LocalDate deadline;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    public @NotBlank(message = "Task name cannot be empty")
    @Size(min = 3, max = 100, message = "Название задачи должно быть от 3 до 100 символов") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Task name cannot be empty")
                        @Size(min = 3, max = 100, message = "Название задачи должно быть от 3 до 100 символов") String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public @NotNull(message = "Project ID is required") Long getProjectId() {
        return projectId;
    }

    public void setProjectId(@NotNull(message = "Project ID is required") Long projectId) {
        this.projectId = projectId;
    }


    public @Future(message = "The deadline must be in the future") LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(@Future(message = "The deadline must be in the future") LocalDate deadline) {
        this.deadline = deadline;
    }
}
