package com.example.projectmanager.controller;

import com.example.projectmanager.dto.request.TaskRequest;
import com.example.projectmanager.dto.response.TaskResponse;
import com.example.projectmanager.entity.Status;
import com.example.projectmanager.entity.Task;
import com.example.projectmanager.entity.User;
import com.example.projectmanager.exception.ResourceNotFoundException;
import com.example.projectmanager.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;

@Controller
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {
    private final TaskService taskService;
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/create-task")
    public String showCreateForm(
            @PathVariable Long projectId,
            Model model) {
        TaskRequest request = new TaskRequest();
        request.setProjectId(projectId);
        model.addAttribute("taskRequest", request);
        return "tasks/create-task";
    }

    @PostMapping("/create-task")
    public String createTask(
            @PathVariable Long projectId,
            @ModelAttribute("taskRequest") TaskRequest request,
            @AuthenticationPrincipal User owner,
            RedirectAttributes redirectAttributes) throws AccessDeniedException {

        if (owner == null) {
            logger.error("Attempt to create a task by an unauthenticated user");
            redirectAttributes.addFlashAttribute("error", "You must be logged in to create a task");
            return "redirect:/login";
        }

        request.setProjectId(projectId);

        try {
            taskService.createTask(request);
            logger.info("Task created successfully by user: {}", owner.getUsername());
            redirectAttributes.addFlashAttribute("success", "The task has been created!");
            return "redirect:/projects/" + projectId;
        } catch (Exception e) {
            logger.error("Error creating task", e);
            redirectAttributes.addFlashAttribute("taskRequest", request);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/projects/" + projectId + "/tasks/create-task";
        }
    }

    @GetMapping("/{taskId}")
    public String getTaskDetails(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            Model model) {
        TaskResponse task = taskService.getTaskById(taskId, projectId);
        model.addAttribute("task", task);
        model.addAttribute("projectId", projectId);
        return "tasks/details-task";
    }

    @GetMapping("/{taskId}/edit-task")
    public String showEditForm(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            Model model) {
        TaskResponse response = taskService.getTaskById(taskId, projectId);
        TaskRequest request = new TaskRequest();
        request.setName(response.getName());
        request.setDescription(response.getDescription());
        request.setDeadline(response.getDeadline());
        request.setProjectId(projectId);

        model.addAttribute("taskId", taskId);
        model.addAttribute("projectId", projectId);
        model.addAttribute("taskRequest", request);
        return "tasks/edit-task";
    }

    @PostMapping("/{taskId}/edit-task")
    public String updateTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @ModelAttribute ("taskRequest") TaskRequest taskRequest,
            @AuthenticationPrincipal User owner,
            RedirectAttributes redirectAttributes) {
        try {
            taskRequest.setProjectId(projectId);
            TaskResponse updatedTask = taskService.updateTask(taskId, taskRequest);
            redirectAttributes.addFlashAttribute("success",
                "Задача '" + updatedTask.getName() + "' успешно обновлена");
            return "redirect:/projects/" + projectId + "/tasks/" + taskId;
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:create-task";
        }
    }

    @PostMapping("/{taskId}/complete")
    public String completeTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @AuthenticationPrincipal User owner,
            TaskRequest request,
            RedirectAttributes redirectAttributes) {

        Status status = taskService.markTaskAsComplete(taskId, projectId);
        String message = status == Status.COMPLETED
                ? "Задача помечена как завершённая"
                : "Задача возвращена в работу";

        redirectAttributes.addFlashAttribute("success", message);
        return "redirect:/projects/" + projectId + "/tasks/" + taskId;
    }

    @PostMapping("/{taskId}/delete")
    public String deleteTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @AuthenticationPrincipal User owner,
            RedirectAttributes redirectAttributes) {
        taskService.deleteTask(taskId, projectId);
        redirectAttributes.addFlashAttribute("success", "Задача удалена");
        return "redirect:/projects/" + projectId;
    }
}
