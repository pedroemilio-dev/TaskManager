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

import com.exemplo.taskmanager.dto.project.CreateProjectRequest;
import com.exemplo.taskmanager.dto.project.EditProjectRequest;
import com.exemplo.taskmanager.dto.project.ProjectResponse;
import com.exemplo.taskmanager.model.User;
import com.exemplo.taskmanager.service.ProjectService;

import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(projectService.getAllProjects(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(projectService.getProject(id, user));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody CreateProjectRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request, user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> editProject(@PathVariable Long id, @RequestBody EditProjectRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(projectService.editProject(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id, @AuthenticationPrincipal User user) {
        projectService.deleteProject(id, user);
        return ResponseEntity.noContent().build();
    }
}