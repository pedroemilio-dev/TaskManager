package com.exemplo.taskmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exemplo.taskmanager.dto.task.CreateTaskRequest;
import com.exemplo.taskmanager.dto.task.EditTaskRequest;
import com.exemplo.taskmanager.dto.task.TaskResponse;
import com.exemplo.taskmanager.model.Project;
import com.exemplo.taskmanager.model.Task;
import com.exemplo.taskmanager.model.User;
import com.exemplo.taskmanager.repository.ProjectRepository;
import com.exemplo.taskmanager.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock private TaskRepository taskRepository;
    @Mock private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;
    private Project project;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .password("hashed_password")
                .build();

        task = Task.builder()
                .id(1L)
                .name("Buy milk")
                .description("At the supermarket!")
                .completed(false)
                .priority(Task.Priority.DEFAULT)
                .user(user)
                .build();

        project = Project.builder()
                .id(2L)
                .name("Groceries")
                .user(user)
                .subProjects(new ArrayList<>())
                .tasks(new ArrayList<>())
                .build();

    }

    // ─── Create a Task ────────────────────────────────────────────
    @Test
    void createTaskSuccess() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setName("Buy almonds");
        request.setPriority(Task.Priority.DEFAULT);

        TaskResponse response = taskService.createTask(request, user);

        assertThat(response.getName()).isEqualTo("Buy almonds");
        assertThat(response.getDescription()).isNull();
        assertThat(response.getPriority()).isEqualTo(Task.Priority.DEFAULT);
        assertThat(response.getDueDate()).isNull();

        verify(projectRepository, never()).findByIdAndUser(any(), any());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTaskSuccessWithProjectId() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setName("Buy almonds");
        request.setPriority(Task.Priority.HIGH);
        request.setProjectId(1L);

        when(projectRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(project));

        TaskResponse response = taskService.createTask(request, user);

        assertThat(response.getName()).isEqualTo("Buy almonds");
        assertThat(response.getDescription()).isNull();
        assertThat(response.getPriority()).isEqualTo(Task.Priority.HIGH);
        assertThat(response.getDueDate()).isNull();

        verify(projectRepository).findByIdAndUser(1L, user);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void gettaskSuccess() {
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getTask(1L, user);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    void getTaskNotFoundFailure() {
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTask(1L, user)).isInstanceOf(RuntimeException.class).hasMessageContaining("Task not found");
    }

    @Test
    void editTaskSuccess() {
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(task));

        EditTaskRequest request = new EditTaskRequest();
        request.setName("Buy butter");

        TaskResponse response = taskService.editTask(1L, request, user);

        assertThat(response.getName()).isEqualTo("Buy butter");

        verify(projectRepository, never()).findByIdAndUser(any(), any());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void editTaskSuccessWithProjectId() {
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(task));
        when(projectRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(project));

        EditTaskRequest request = new EditTaskRequest();
        request.setProjectId(2L);

        TaskResponse response = taskService.editTask(1L, request, user);

        assertThat(response.getProjectId()).isEqualTo(2L);

        verify(projectRepository).findByIdAndUser(2L, user);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void editTaskTaskNotFoundFailure() {
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        EditTaskRequest request = new EditTaskRequest();
        request.setName("Buy eggs");

        assertThatThrownBy(() -> taskService.editTask(1L, request, user)).isInstanceOf(RuntimeException.class).hasMessageContaining("Task not found");
    }

    @Test
    void editTaskProjectNotFoundFailure() {
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(task));
        when(projectRepository.findByIdAndUser(2L, user)).thenReturn(Optional.empty());

        EditTaskRequest request = new EditTaskRequest();
        request.setProjectId(2L);

        assertThatThrownBy(() -> taskService.editTask(1L, request, user)).isInstanceOf(RuntimeException.class).hasMessageContaining("Project not found");
    }

    @Test
    void deleteTaskSuccess() {
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L, user);

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTaskTaskNotFoundFailure() {
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(1L, user)).isInstanceOf(RuntimeException.class).hasMessageContaining("Task not found");
    }

    @Test
    void toggleTaskSuccess() {
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.toggleTask(1L, user);

        assertThat(response.getName()).isEqualTo("Buy milk");
        assertThat(response.getDescription()).isEqualTo("At the supermarket!");
        assertThat(response.getPriority()).isEqualTo(Task.Priority.DEFAULT);
        assertThat(response.isCompleted()).isTrue();

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void toggleTaskTaskNotFoundFailure() {
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.toggleTask(1L, user)).isInstanceOf(RuntimeException.class).hasMessageContaining("Task not found");
    }

    @Test
    void getInboxTasksSuccess() {
        when(taskRepository.findByUserAndProjectIsNull(user)).thenReturn(List.of(task));

        List<TaskResponse> response = taskService.getInboxTasks(user);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("Buy milk");
    }

    @Test
    void getInboxTasksEmpty() {
        when(taskRepository.findByUserAndProjectIsNull(user)).thenReturn(List.of());

        List<TaskResponse> response = taskService.getInboxTasks(user);

        assertThat(response).isEmpty();
    }
}
