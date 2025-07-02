package com.example.projectmanager.controller;

import com.example.projectmanager.dto.request.ProjectRequest;
import com.example.projectmanager.dto.response.ProjectResponse;
import com.example.projectmanager.entity.User;
import com.example.projectmanager.service.ProjectService;
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

        projectService.markProjectAsCompleted(id, owner);
        redirectAttributes.addFlashAttribute("success", "Проект помечен как завершённый");
        return "redirect:/details/" + id;
    }
}
