package com.exemplo.taskmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exemplo.taskmanager.dto.task.CreateTaskRequest;
import com.exemplo.taskmanager.dto.task.EditTaskRequest;
import com.exemplo.taskmanager.dto.task.TaskResponse;
import com.exemplo.taskmanager.model.Project;
import com.exemplo.taskmanager.model.Task;
import com.exemplo.taskmanager.model.User;
import com.exemplo.taskmanager.repository.ProjectRepository;
import com.exemplo.taskmanager.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request, User user) {

        Project project = null;
        if (request.getProjectId() != null) {
            project = projectRepository.findByIdAndUser(request.getProjectId(), user)
                    .orElseThrow(() -> new RuntimeException("Project not found"));
        }

        Task task = Task.builder()
                .name(request.getName())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .priority(request.getPriority() != null ? request.getPriority() : Task.Priority.DEFAULT)
                .user(user)
                .project(project)
                .build();

        taskRepository.save(task);

        return toTaskResponse(task);
    }

    @Transactional
    public TaskResponse getTask(Long taskId, User user) {
        Task task = taskRepository.findByIdAndUser(taskId, user).orElseThrow(() -> new RuntimeException("Task not found"));

        return toTaskResponse(task);
    }

    @Transactional
    public TaskResponse editTask(Long taskId, EditTaskRequest request, User user) {
        Task task = taskRepository.findByIdAndUser(taskId, user).orElseThrow(() -> new RuntimeException("Task not found"));

        if(request.getName() != null) task.setName(request.getName());
        if(request.getDescription() != null) task.setDescription(request.getDescription());
        if(request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if(request.getPriority() != null) task.setPriority(request.getPriority());
        if(request.getProjectId() != null) {
            Project project = projectRepository.findByIdAndUser(request.getProjectId(), user).orElseThrow(() -> new RuntimeException("Project not found"));
            task.setProject(project);
        }

        taskRepository.save(task);
        return toTaskResponse(task);
    }

    @Transactional
    public void deleteTask(Long taskId, User user) {
        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }

    @Transactional
    public TaskResponse toggleTask(Long taskId, User user) {
        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setCompleted(!task.isCompleted());
        taskRepository.save(task);
        return toTaskResponse(task);
    }

    public List<TaskResponse> getInboxTasks(User user) {
        return taskRepository.findByUserAndProjectIsNull(user)
                .stream()
                .map(this::toTaskResponse)
                .toList();
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