package com.example.projectmanager.repositorу;

import com.example.projectmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId();
    Task findByIdAndProjectId();
    int countByProjectAndStatus();
    // пакетное обновление статусов при завершении проекта
    void updateStatusByProject();
}
