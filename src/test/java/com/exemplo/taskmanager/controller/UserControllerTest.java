package com.exemplo.taskmanager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.exemplo.taskmanager.dto.user.ChangePasswordRequest;
import com.exemplo.taskmanager.dto.user.UpdateUserRequest;
import com.exemplo.taskmanager.dto.user.UserResponse;
import com.exemplo.taskmanager.service.UserService;
import com.exemplo.taskmanager.security.JwtUtil;
import com.exemplo.taskmanager.security.SecurityEventLogger;
import com.exemplo.taskmanager.service.UserDetailsServiceImpl;
import com.exemplo.taskmanager.repository.BlacklistedTokenRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @MockitoBean
    private SecurityEventLogger securityEventLogger;

    // --- GET /users/me ---

    @Test
    @WithMockUser
    void getProfile_authenticated_returnsOk() throws Exception {
        UserResponse response = new UserResponse(1L, "Pedro", "pedro@example.com");
        when(userService.getProfile(any())).thenReturn(response);

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pedro"))
                .andExpect(jsonPath("$.email").value("pedro@example.com"));
    }

    // --- PATCH /users/me ---

    @Test
    @WithMockUser
    void updateProfile_validInput_returnsOk() throws Exception {
        UpdateUserRequest dto = new UpdateUserRequest();
        dto.setName("Pedro Updated");

        UserResponse response = new UserResponse(1L, "Pedro Updated", "pedro@example.com");
        when(userService.updateProfile(any(), any())).thenReturn(response);

        mockMvc.perform(patch("/api/users/me")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pedro Updated"));
    }

    // --- PATCH /users/me/password ---

    @Test
    @WithMockUser
    void changePassword_validInput_returnsNoContent() throws Exception {
        ChangePasswordRequest dto = new ChangePasswordRequest();
        dto.setCurrentPassword("oldpass");
        dto.setNewPassword("newpass");

        mockMvc.perform(patch("/api/users/me/password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(userService).changePassword(any(), any());
    }

    @Test
    @WithMockUser
    void changePassword_wrongCurrentPassword_throwsException() throws Exception {
        ChangePasswordRequest dto = new ChangePasswordRequest();
        dto.setCurrentPassword("wrongpass");
        dto.setNewPassword("newpass");

        doThrow(new RuntimeException("Password atual incorreta"))
                .when(userService).changePassword(any(), any());

        mockMvc.perform(patch("/api/users/me/password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    // --- DELETE /users/me ---

    @Test
    @WithMockUser
    void deleteAccount_authenticated_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/me")
                .with(csrf())
                .header("Authorization", "Bearer some-token"))
                .andExpect(status().isNoContent());

        verify(userService).deleteAccount(any(), eq("some-token"));
    }
}