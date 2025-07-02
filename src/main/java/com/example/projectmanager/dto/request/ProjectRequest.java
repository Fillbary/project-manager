package com.example.projectmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProjectRequest {
    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 100, message = "The project name must contain at least three characters.")
    private String name;

    private String description;

    public void setId(Long id) {
        this.id = id;
    }

    private Long id;

    public @NotBlank(message = "Project name is required")
            @Size(min = 3, message = "The project name must contain at least three characters.") String getName() {
        return name;
    }

    public void setName(
            @NotBlank(message = "Project name is required")
            @Size(min = 3, message = "The project name must contain at least three characters.") String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
