package com.exemplo.taskmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.exemplo.taskmanager.dto.project.CreateProjectRequest;
import com.exemplo.taskmanager.dto.project.EditProjectRequest;
import com.exemplo.taskmanager.dto.project.ProjectResponse;
import com.exemplo.taskmanager.dto.task.TaskResponse;
import com.exemplo.taskmanager.exception.BusinessRuleException;
import com.exemplo.taskmanager.exception.ResourceNotFoundException;
import com.exemplo.taskmanager.model.Project;
import com.exemplo.taskmanager.model.Task;
import com.exemplo.taskmanager.model.User;
import com.exemplo.taskmanager.repository.ProjectRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, User user) {
        Project parent = null;
        if(request.getParentId() != null) {
            parent = projectRepository.findByIdAndUser(request.getParentId(), user).orElseThrow(() -> new RuntimeException("Project not found"));
            
            if(parent.getParent() != null) {
                throw new RuntimeException("Subprojects cannot have subprojects");
            }
        }

        Project project = Project.builder()
                .name(request.getName())
                .color(request.getColor())
                .user(user)
                .parent(parent)
                .build();

        projectRepository.save(project);

        return toResponse(project);
    }

    @Transactional
    public ProjectResponse getProject(Long id, User user) {
        Project project = projectRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        return toResponse(project);
    }

    public List<ProjectResponse> getAllProjects(User user) {
        return projectRepository.findByUserAndParentIsNull(user).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ProjectResponse editProject(Long id, EditProjectRequest request, User user) {
        Project project = projectRepository.findByIdAndUser(id, user).orElseThrow(() -> new RuntimeException("Project not found"));

        if(request.getName() != null) project.setName(request.getName());
        if(request.getColor() != null) project.setColor(request.getColor());

        if(request.getParentId() != null) {
            if(request.getParentId() == 0) {
                project.setParent(null);
            } else {
                Project newParent = projectRepository.findByIdAndUser(request.getParentId(), user).orElseThrow(() -> new RuntimeException("Project not found"));

                if(newParent.getParent() != null) {
                    throw new BusinessRuleException("Subprojects cannot have subprojects");
                }

                project.setParent(newParent);
            }
        }

        projectRepository.save(project);

        return toResponse(project);
    }

    @Transactional
    public void deleteProject(Long id, User user) {
        Project project = projectRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        projectRepository.delete(project);
    }

    private ProjectResponse toResponse(Project project) {
        List<ProjectResponse> subProjects = project.getSubProjects().stream()
                .map(this::toResponse)
                .toList();

        List<TaskResponse> tasks = project.getTasks().stream()
            .map(this::toTaskResponse)
            .toList();

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getColor(),
                project.getParent() != null ? project.getParent().getId() : null,
                subProjects,
                tasks
        );
    }

    private TaskResponse toTaskResponse(Task task) {
    return new TaskResponse(
            task.getId(),
            task.getName(),
            task.getDescription(),
            task.isCompleted(),
            task.getPriority(),
            task.getDueDate(),
            task.getCreatedAt(),
            task.getProject() != null ? task.getProject().getId() : null
    );
}
}
