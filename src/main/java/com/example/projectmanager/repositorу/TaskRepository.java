package com.example.projectmanager.repositorу;

import com.example.projectmanager.entity.Project;
import com.example.projectmanager.entity.Status;
import com.example.projectmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllTasksByProjectId(Long id);
    Optional<Task> findByIdAndProjectId(Long id, Long projectId);
    int countByProject(Project project);
}
