package com.example.projectmanager.entity;

public enum Status {
    IN_PROGRESS(false), COMPLETED(true);

    private final boolean completed;

    Status(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }
}


