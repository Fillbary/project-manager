package com.example.projectmanager.controller;

import com.example.projectmanager.dto.request.ProjectRequest;
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
            return "redirect:/projects-list";
        } catch (Exception e) {
            logger.error("Error creating project", e);
            redirectAttributes.addFlashAttribute("projectRequest", request);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/create";
        }
    }

    @GetMapping("/details/{id}/edit-project")
    public String showEditForm(
            @PathVariable Long id,
            @AuthenticationPrincipal User owner,
            Model model) {

        ProjectResponse project = projectService.getProjectById(id, owner);
        ProjectRequest updateRequest = new ProjectRequest();
        updateRequest.setName(project.getName());
        updateRequest.setDescription(project.getDescription());
        updateRequest.setStatus(project.getStatus());

        model.addAttribute("projectId", id);
        model.addAttribute("updateRequest", updateRequest);

        return "projects/edit-project";
    }

    @PostMapping("/details/{id}/edit-project")
    public String updateProject(
            @PathVariable Long id,
            @Valid @ModelAttribute("updateRequest") ProjectRequest updateRequest,
            @AuthenticationPrincipal User owner,
            RedirectAttributes redirectAttributes) {

        try {
            updateRequest.setOwner(owner);
            Project updatedProject = projectService.updateProject(id, updateRequest);
            redirectAttributes.addFlashAttribute("success",
                "Проект '" + updatedProject.getName() + "' успешно обновлен");
            return "redirect:/details/" + id;
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/details/" + id + "/edit-project";
        }
    }

    @GetMapping("/projects-list")
    public String getUserProjects(
            @AuthenticationPrincipal User owner,
            Model model) {
        List<ProjectResponse> projects = projectService.getProjectsByOwner(owner);
        model.addAttribute("projects", projects);
        return "projects/projects-list";
    }

    @GetMapping("/details/{id}")
    public String getProjectDetails(
            @PathVariable Long id,
            @AuthenticationPrincipal User owner,
            Model model) {

        ProjectResponse project = projectService.getProjectById(id, owner);
        model.addAttribute("project", project);

        return "projects/details";
    }

    @PostMapping("/details/{id}/complete")
    public String completeProject(
            @PathVariable Long id,
            @AuthenticationPrincipal User owner,
            RedirectAttributes redirectAttributes) {

        Status status = projectService.markProjectAsCompleted(id, owner);
        String message = status == Status.COMPLETED
                ? "Проект помечен как завершённый"
                : "Проект возвращён в работу";

        redirectAttributes.addFlashAttribute("success", message);
        return "redirect:/details/" + id;
    }

    @PostMapping("/details/{id}/delete")
    public String deleteProject(
            @PathVariable Long id,
            @AuthenticationPrincipal User owner,
            RedirectAttributes redirectAttributes) {

        projectService.deleteProject(id, owner);
        redirectAttributes.addFlashAttribute("success", "Проект удален");
        return "redirect:/projects-list";
    }
}
