package com.example.projectmanager.service;

import com.example.projectmanager.dto.request.ProjectRequest;
import com.example.projectmanager.dto.response.ProjectResponse;
import com.example.projectmanager.entity.Project;
import com.example.projectmanager.entity.Status;
import com.example.projectmanager.entity.User;
import com.example.projectmanager.exception.ConflictException;
import com.example.projectmanager.exception.ResourceNotFoundException;
import com.example.projectmanager.repositorу.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository PROJECT_REPOSITORY;

    public ProjectService(ProjectRepository projectRepository) {
        this.PROJECT_REPOSITORY = projectRepository;
    }

    @Transactional
    public void createProject (ProjectRequest request, User owner) throws AccessDeniedException {

        if(request.getName() == null || request.getName().isEmpty()) {
            throw new IllegalArgumentException("Project name is required");
        }
        if (PROJECT_REPOSITORY.existsByNameAndOwner_Id(request.getName(), owner.getId())) {
            throw new ConflictException("A project with this name already exists");
        }
        if (owner == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        Project project = mapToProject(request, owner);
        PROJECT_REPOSITORY.save(project);
    }

    public Project updateProject(Long id, ProjectRequest request) {
        Project project = PROJECT_REPOSITORY.findByIdAndOwner(id, request.getOwner())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Project not found with id: " + id + " and owner: " + request.getOwner().getUsername()));

        if (!project.getName().equals(request.getName())) {
            project.setName(request.getName());
        }

        if (request.getDescription() != null &&
            !request.getDescription().equals(project.getDescription())) {
            project.setDescription(request.getDescription());
        }

        return PROJECT_REPOSITORY.save(project);
    }

    public Status markProjectAsCompleted(Long id, User owner) {
        Project project = PROJECT_REPOSITORY.findByIdAndOwner(id, owner)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Project not found with id: " + id + " and owner: " + owner.getUsername()));

        Status status = project.getStatus() == Status.COMPLETED ? Status.IN_PROGRESS : Status.COMPLETED;

        project.setStatus(status);
        PROJECT_REPOSITORY.save(project);
        return status;
    }

    public void deleteProject(Long id, User owner) {
        Project project = PROJECT_REPOSITORY.findByIdAndOwner(id, owner)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Project not found with id: " + id + " and owner: " + owner.getUsername()));

        PROJECT_REPOSITORY.delete(project);
    }

    public List<ProjectResponse> getProjectsByOwner(User owner) {
        List<Project> projects = PROJECT_REPOSITORY.findAllByOwner(owner);
        List<ProjectResponse> responseList = new ArrayList<>();
        //Переписать как stream
        //Преобразуем каждый проект в ответ и добавляем в список ответов
        for (Project project : projects) {
            responseList.add(mapToProjectResponse(project));
        }
        return responseList;
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id, User owner) {
        Project project = PROJECT_REPOSITORY.findByIdAndOwner(id, owner)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Project not found with id: " + id + " and owner: " + owner.getUsername()));

        return mapToProjectResponse(project);
    }

    private ProjectResponse mapToProjectResponse(Project project) {
        return new ProjectResponse(
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getCreatedAt(),
            project.getUpdatedAt(),
            project.getOwner().getUsername(),
            project.getStatus()
        );
    }

    private Project mapToProject(ProjectRequest request, User owner) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOwner(owner);
        project.setCreatedAt(LocalDateTime.now());
        project.setStatus(Status.IN_PROGRESS);
        return project;
    }
}
