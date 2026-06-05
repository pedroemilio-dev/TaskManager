package com.exemplo.taskmanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.taskmanager.dto.task.CreateTaskRequest;
import com.exemplo.taskmanager.dto.task.EditTaskRequest;
import com.exemplo.taskmanager.dto.task.TaskResponse;
import com.exemplo.taskmanager.model.User;
import com.exemplo.taskmanager.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/inbox")
    public ResponseEntity<List<TaskResponse>> getInboxTasks(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.getInboxTasks(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.getTask(id, user));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request, user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> editTask(@PathVariable Long id, @RequestBody EditTaskRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.editTask(id, request, user));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TaskResponse> toggleTask(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.toggleTask(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @AuthenticationPrincipal User user) {
        taskService.deleteTask(id, user);
        return ResponseEntity.noContent().build();
    }
}