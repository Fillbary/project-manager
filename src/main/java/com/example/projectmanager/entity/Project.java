package com.example.projectmanager.entity;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    @Size(max = 100)
    private String name;

    @Column
    @Size(max = 500)
    private String description;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column
    private Status status;


    public Project(String name, String description, LocalDateTime createdAt, User owner) {
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.owner = owner;
        this.status = Status.IN_PROGRESS;
    }

    public Project() {}

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public @NotNull @Size(max = 100) String getName() {
        return name;
    }

    public void setName(@NotNull @Size(max = 100) String name) {
        this.name = name;
    }

    public @Size(max = 500) String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 500) String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
