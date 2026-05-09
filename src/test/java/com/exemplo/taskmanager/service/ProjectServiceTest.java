package com.exemplo.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exemplo.taskmanager.dto.project.CreateProjectRequest;
import com.exemplo.taskmanager.dto.project.EditProjectRequest;
import com.exemplo.taskmanager.dto.project.ProjectResponse;
import com.exemplo.taskmanager.model.Project;
import com.exemplo.taskmanager.model.User;
import com.exemplo.taskmanager.repository.ProjectRepository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private User user;
    private Project project;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .password("hashed_password")
                .build();

        project = Project.builder()
                .id(2L)
                .name("Groceries")
                .user(user)
                .subProjects(new ArrayList<>())
                .tasks(new ArrayList<>())
                .build();
    }

    @Test
    void createProjectSuccess() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Groceries");
        request.setColor("Green");

        ProjectResponse response = projectService.createProject(request, user);

        assertThat(response.getName()).isEqualTo("Groceries");
        assertThat(response.getColor()).isEqualTo("Green");

        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void createSubProjectSuccess() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Date night");
        request.setColor("Red");
        request.setParentId(2L);

        when(projectRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(project));

        ProjectResponse response = projectService.createProject(request, user);

        assertThat(response.getName()).isEqualTo("Date night");
        assertThat(response.getColor()).isEqualTo("Red");
        assertThat(response.getParentId()).isEqualTo(2L);

        verify(projectRepository).findByIdAndUser(2L, user);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void createSubProjectFailure() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Work");
        request.setColor("Blue");
        request.setParentId(2L);

        when(projectRepository.findByIdAndUser(2L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.createProject(request, user)).isInstanceOf(RuntimeException.class).hasMessageContaining("Project not found");

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void createSubProjectWithParentFailure() {
        Project grandParent = Project.builder()
                .id(3L)
                .name("Grandparent")
                .user(user)
                .subProjects(new ArrayList<>())
                .tasks(new ArrayList<>())
                .build();

        project.setParent(grandParent);

        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Cooking 2");
        request.setColor("Black");
        request.setParentId(2L);

        when(projectRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(project));
        
        assertThatThrownBy(() -> projectService.createProject(request, user)).isInstanceOf(RuntimeException.class).hasMessageContaining("Subprojects cannot have subprojects");

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void getProjectSuccess() {
        when(projectRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(project));

        ProjectResponse response = projectService.getProject(2L, user);

        assertThat(response.getName()).isEqualTo("Groceries");
    }

    @Test
    void getProjectNotFoundFailure() {
        when(projectRepository.findByIdAndUser(2L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProject(2L, user)).isInstanceOf(RuntimeException.class).hasMessageContaining("Project not found");
    }

    @Test
    void getAllProjectsSuccess() {
        when(projectRepository.findByUserAndParentIsNull(user)).thenReturn(List.of(project));

        List<ProjectResponse> response = projectService.getAllProjects(user);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("Groceries");
    }

    @Test
    void getAllProjectsEmpty() {
        when(projectRepository.findByUserAndParentIsNull(user)).thenReturn(List.of());

        List<ProjectResponse> response = projectService.getAllProjects(user);

        assertThat(response).isEmpty();
    }

    @Test
    void editProjectSuccess() {
        EditProjectRequest request = new EditProjectRequest();
        request.setName("New project");

        when(projectRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(project));

        ProjectResponse response = projectService.editProject(2L, request, user);

        assertThat(response.getName()).isEqualTo("New project");

        verify(projectRepository).findByIdAndUser(2L, user);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void editProjectToSubProjectSuccess() {
        Project grandParent = Project.builder()
                .id(3L)
                .name("Grandparent")
                .user(user)
                .subProjects(new ArrayList<>())
                .tasks(new ArrayList<>())
                .build();

        EditProjectRequest request = new EditProjectRequest();
        request.setParentId(3L);

        when(projectRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(project));
        when(projectRepository.findByIdAndUser(3L, user)).thenReturn(Optional.of(grandParent));

        ProjectResponse response = projectService.editProject(2L, request, user);

        assertThat(response.getParentId()).isEqualTo(3L);

        verify(projectRepository).findByIdAndUser(2L, user);
        verify(projectRepository).findByIdAndUser(3L, user);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void editProjectSubProjectFailure() {
        EditProjectRequest request = new EditProjectRequest();
        request.setParentId(3L);

        when(projectRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(project));
        when(projectRepository.findByIdAndUser(3L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.editProject(2L, request, user)).isInstanceOf(RuntimeException.class).hasMessageContaining("Project not found");

        verify(projectRepository).findByIdAndUser(2L, user);
        verify(projectRepository).findByIdAndUser(3L, user);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void editProjectSubProjectAlreadyHasParentFailure() {
        Project grandParent = Project.builder()
                .id(3L)
                .name("Grandparent")
                .user(user)
                .subProjects(new ArrayList<>())
                .tasks(new ArrayList<>())
                .build();

            Project son = Project.builder()
                .id(4L)
                .name("Son")
                .user(user)
                .subProjects(new ArrayList<>())
                .tasks(new ArrayList<>())
                .build();

        project.setParent(grandParent);

        EditProjectRequest request = new EditProjectRequest();
        request.setName("Project");
        request.setColor("Gold");
        request.setParentId(2L);

        when(projectRepository.findByIdAndUser(4L, user)).thenReturn(Optional.of(son));
        when(projectRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.editProject(4L, request, user)).isInstanceOf(RuntimeException.class).hasMessageContaining("Subprojects cannot have subprojects");

        verify(projectRepository).findByIdAndUser(4L, user);
        verify(projectRepository).findByIdAndUser(2L, user);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void deleteProjectSuccess() {
        when(projectRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(project));

        projectService.deleteProject(2L, user);

        verify(projectRepository).findByIdAndUser(2L, user);
        verify(projectRepository).delete(project);
    }

    @Test
    void deleteProjectNotFoundFailure() {
        when(projectRepository.findByIdAndUser(2L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.deleteProject(2L, user)).isInstanceOf(RuntimeException.class).hasMessageContaining("Project not found");

        verify(projectRepository).findByIdAndUser(2L, user);
    }
}
