package com.example.projectmanager.controller;

import com.example.projectmanager.entity.User;
import com.example.projectmanager.service.ProjectService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class HomeController {
    private final ProjectService projectService;

    public HomeController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public String home(
            @AuthenticationPrincipal User owner,
            Model model
    ) {
        model.addAttribute("projects", projectService.getProjectsByOwner(owner));
        return "home";
    }
}
