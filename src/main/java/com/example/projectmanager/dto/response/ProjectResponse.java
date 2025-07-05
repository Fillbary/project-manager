package com.example.projectmanager.dto.response;

import com.example.projectmanager.entity.Status;

import java.time.LocalDateTime;

public class ProjectResponse {
    private long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private String ownerName;
    private Status status;

    public ProjectResponse(long id, String name, String description, LocalDateTime createdAd, String ownerName, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAd;
        this.ownerName = ownerName;
        this.status = status;
    }

    public ProjectResponse() {}

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
