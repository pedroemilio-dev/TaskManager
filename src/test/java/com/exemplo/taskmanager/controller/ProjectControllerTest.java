package com.exemplo.taskmanager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

import com.exemplo.taskmanager.dto.project.CreateProjectRequest;
import com.exemplo.taskmanager.dto.project.EditProjectRequest;
import com.exemplo.taskmanager.dto.project.ProjectResponse;
import com.exemplo.taskmanager.service.ProjectService;
import com.exemplo.taskmanager.security.JwtUtil;
import com.exemplo.taskmanager.security.SecurityEventLogger;
import com.exemplo.taskmanager.service.UserDetailsServiceImpl;
import com.exemplo.taskmanager.repository.BlacklistedTokenRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @MockitoBean
    private SecurityEventLogger securityEventLogger;

    // --- GET /projects ---

    @Test
    @WithMockUser
    void getAllProjects_authenticated_returnsOk() throws Exception {
        when(projectService.getAllProjects(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // --- GET /projects/{id} ---

    @Test
    @WithMockUser
    void getProject_existing_returnsOk() throws Exception {
        ProjectResponse response = new ProjectResponse(1L, "My Project", "#ff0000", null, List.of(), List.of());
        when(projectService.getProject(eq(1L), any())).thenReturn(response);

        mockMvc.perform(get("/api/projects/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("My Project"));
    }

    // --- POST /projects ---

    @Test
    @WithMockUser
    void createProject_validInput_returnsCreated() throws Exception {
        CreateProjectRequest dto = new CreateProjectRequest();
        dto.setName("New Project");

        ProjectResponse response = new ProjectResponse(1L, "New Project", null, null, List.of(), List.of());
        when(projectService.createProject(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Project"));
    }

    // --- PATCH /projects/{id} ---

    @Test
    @WithMockUser
    void editProject_validInput_returnsOk() throws Exception {
        EditProjectRequest dto = new EditProjectRequest();
        dto.setName("Updated Project");

        ProjectResponse response = new ProjectResponse(1L, "Updated Project", null, null, List.of(), List.of());
        when(projectService.editProject(eq(1L), any(), any())).thenReturn(response);

        mockMvc.perform(patch("/api/projects/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Project"));
    }

    // --- DELETE /projects/{id} ---

    @Test
    @WithMockUser
    void deleteProject_existing_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/projects/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(projectService).deleteProject(eq(1L), any());
    }
}