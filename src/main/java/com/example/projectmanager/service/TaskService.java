package com.example.projectmanager.service;

import com.example.projectmanager.controller.ProjectController;
import com.example.projectmanager.dto.request.TaskRequest;
import com.example.projectmanager.dto.response.TaskResponse;
import com.example.projectmanager.entity.Project;
import com.example.projectmanager.entity.Status;
import com.example.projectmanager.entity.Task;
import com.example.projectmanager.entity.User;
import com.example.projectmanager.exception.ConflictException;
import com.example.projectmanager.exception.ResourceNotFoundException;
import com.example.projectmanager.repositorу.ProjectRepository;
import com.example.projectmanager.repositorу.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Task createTask(TaskRequest request) throws AccessDeniedException {
        if(request.getName() == null || request.getName().isEmpty()) {
            throw new IllegalArgumentException("Task name is required");
        }

        logger.debug("Creating task for project ID: {}", request.getProjectId());
        Project project = availabilityProjectCheck(request);
        logger.debug("Found project: {}", project != null ? project.getId() : "null");

        Task task = mapToTask(request, project);
        logger.debug("Task before save - Project ID: {}", task.getProject() != null ? task.getProject().getId() : "null");
        return taskRepository.save(task);
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Project project = availabilityProjectCheck(request);
        Task task = availabilityTaskCheck(id, project.getId());

        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setDeadline(request.getDeadline());

        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    public Status markTaskAsComplete(Long id, Long projectId) {
        Task task = availabilityTaskCheck(id, projectId);

        Status status = task.getStatus() == Status.COMPLETED
                ? Status.IN_PROGRESS
                : Status.COMPLETED;

        task.setStatus(status);
        taskRepository.save(task);
        return status;
    }

    @Transactional
    public void completeAllTasks(Long projectId) {
        List<Task> tasks = taskRepository.findAllTasksByProjectId(projectId);
        for(Task task : tasks) {
            if(task.getStatus() != Status.COMPLETED) {
                task.setStatus(Status.COMPLETED);
            }
        }
        taskRepository.saveAll(tasks);
    }

    public void deleteTask(Long id, Long projectId) {
        Task task = availabilityTaskCheck(id, projectId);
        taskRepository.delete(task);
    }

    public List<TaskResponse> getTasksByProjects(Project project) {
        List<Task> tasks = taskRepository.findAllTasksByProjectId(project.getId());
        List<TaskResponse> responseList = new ArrayList<>();
        // Переписать как стрим
        for (Task task : tasks) {
            responseList.add(mapToTaskResponse(task));
        }
        return responseList;
    }

    @Transactional
    public TaskResponse getTaskById(Long id, Long projectId) {
        Task task = availabilityTaskCheck(id, projectId);
        return mapToTaskResponse(task);
    }

    private TaskResponse mapToTaskResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getStatus(),
                task.getDeadline(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getProject().getId(),
                task.getProject().getName()
        );
    }

    private Task mapToTask(TaskRequest request, Project project) {
        Task task = new Task();
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setDeadline(request.getDeadline());
        task.setStatus(Status.IN_PROGRESS);
        task.setProject(project);
        return task;
    }

    private Task availabilityTaskCheck(Long id, Long projectId){
        return taskRepository.findByIdAndProjectId(id, projectId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Task not found with id: " + id + " and project: " + projectId));
    }

    private Project availabilityProjectCheck(TaskRequest request) {
        if (request.getProjectId() == null) {
            throw new IllegalArgumentException("Project ID is required");
        }
        return projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ConflictException("The project does not exist"));
    }
}
