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

import com.exemplo.taskmanager.dto.task.CreateTaskRequest;
import com.exemplo.taskmanager.dto.task.TaskResponse;
import com.exemplo.taskmanager.service.TaskService;
import com.exemplo.taskmanager.security.JwtUtil;
import com.exemplo.taskmanager.service.UserDetailsServiceImpl;
import com.exemplo.taskmanager.repository.BlacklistedTokenRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private BlacklistedTokenRepository blacklistedTokenRepository;

    // ─── GET /inbox ───

    @Test
    @WithMockUser
    void getInboxTasksAuthenticatedReturnsOk() throws Exception {
        when(taskService.getInboxTasks(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/tasks/inbox"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ── POST /tasks ───

    @Test
    @WithMockUser
    void createTaskValidInputReturnsCreated() throws Exception {
        CreateTaskRequest dto = new CreateTaskRequest();
        dto.setName("Buy milk");

        TaskResponse response = new TaskResponse(1L, "Buy milk", null, false, null, null, null, null);
        when(taskService.createTask(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Buy milk"));
    }

    @Test
    @WithMockUser
    void createTaskMissingTitleReturnsBadRequest() throws Exception {
        CreateTaskRequest dto = new CreateTaskRequest();

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // ─── DELETE ───

    @Test
    @WithMockUser
    void deleteTaskExistingReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/tasks/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(eq(1L), any());
    }
}