package com.example.projectmanager.controller;

import com.example.projectmanager.dto.request.ProjectRequest;
import com.example.projectmanager.dto.request.TaskRequest;
import com.example.projectmanager.dto.response.ProjectResponse;
import com.example.projectmanager.entity.Project;
import com.example.projectmanager.entity.Status;
import com.example.projectmanager.entity.User;
import com.example.projectmanager.exception.ResourceNotFoundException;
import com.example.projectmanager.service.ProjectService;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.slf4j.Logger;

@Controller
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("projectRequest", new ProjectRequest());
        return "projects/create";
    }

    @PostMapping("/create")
    public String createProject(
            @ModelAttribute("projectRequest") ProjectRequest request,
            @AuthenticationPrincipal User owner,
            RedirectAttributes redirectAttributes) throws AccessDeniedException {

        if (owner == null) {
            logger.error("Attempt to create a project by an unauthenticated user");
            redirectAttributes.addFlashAttribute("error", "You must be logged in to create a project");
            return "redirect:/login";
        }

        try {
            projectService.createProject(request, owner);
            logger.info("Project created successfully by user: {}", owner.getUsername());
            redirectAttributes.addFlashAttribute("success", "The project has been created!");
            return "redirect:/projects";
        } catch (Exception e) {
            logger.error("Error creating project", e);
            redirectAttributes.addFlashAttribute("projectRequest", request);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/projects/create";
        }
    }

    @GetMapping
    public String getUserProjects(
            @AuthenticationPrincipal User owner,
            Model model) {
        List<ProjectResponse> projects = projectService.getProjectsByOwner(owner);
        model.addAttribute("projects", projects);
        return "projects/projects-list";
    }

    @GetMapping("/{projectId}")
    public String getProjectDetails(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User owner,
            Model model) {

        ProjectResponse project = projectService.getProjectByIdWithTasks(projectId, owner);
        model.addAttribute("project", project);
        model.addAttribute("taskRequest", new TaskRequest());

        return "projects/details";
    }

    @GetMapping("/{projectId}/edit-project")
    public String showEditForm(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User owner,
            Model model) {

        ProjectResponse project = projectService.getProjectByIdWithTasks(projectId, owner);
        ProjectRequest updateRequest = new ProjectRequest();
        updateRequest.setName(project.getName());
        updateRequest.setDescription(project.getDescription());
        updateRequest.setStatus(project.getStatus());

        model.addAttribute("projectId", projectId);
        model.addAttribute("updateRequest", updateRequest);

        return "projects/edit-project";
    }

    @PostMapping("/{projectId}/edit-project")
    public String updateProject(
            @PathVariable Long projectId,
            @Valid @ModelAttribute("updateRequest") ProjectRequest updateRequest,
            @AuthenticationPrincipal User owner,
            RedirectAttributes redirectAttributes) {

        try {
            updateRequest.setOwner(owner);
            Project updatedProject = projectService.updateProject(projectId, updateRequest);
            redirectAttributes.addFlashAttribute("success",
                "Проект '" + updatedProject.getName() + "' успешно обновлен");
            return "redirect:/details/" + projectId;
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/details/" + projectId + "/edit-project";
        }
    }

    @PostMapping("/{projectId}/complete")
    public String completeProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User owner,
            RedirectAttributes redirectAttributes) {

        Status status = projectService.markProjectAsCompleted(projectId, owner);
        String message = status == Status.COMPLETED
                ? "Проект помечен как завершённый"
                : "Проект возвращён в работу";

        redirectAttributes.addFlashAttribute("success", message);
        return "redirect:/details/" + projectId;
    }

    @PostMapping("/{projectId}/delete")
    public String deleteProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User owner,
            RedirectAttributes redirectAttributes) {

        projectService.deleteProject(projectId, owner);
        redirectAttributes.addFlashAttribute("success", "Проект удален");
        return "redirect:/projects-list";
    }
}
