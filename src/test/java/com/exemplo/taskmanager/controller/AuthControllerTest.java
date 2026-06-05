package com.exemplo.taskmanager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.exemplo.taskmanager.dto.auth.AuthResponse;
import com.exemplo.taskmanager.dto.auth.LoginRequest;
import com.exemplo.taskmanager.dto.auth.RefreshTokenRequest;
import com.exemplo.taskmanager.dto.auth.RegisterRequest;
import com.exemplo.taskmanager.service.UserService;
import com.exemplo.taskmanager.security.JwtUtil;
import com.exemplo.taskmanager.security.SecurityEventLogger;
import com.exemplo.taskmanager.service.UserDetailsServiceImpl;
import com.exemplo.taskmanager.repository.BlacklistedTokenRepository;

import org.springframework.security.authentication.BadCredentialsException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

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

    // --- POST /register ---

    @Test
    void register_validInput_returnsCreated() throws Exception {
        RegisterRequest dto = new RegisterRequest();
        dto.setName("Pedro");
        dto.setEmail("pedro@example.com");
        dto.setPassword("Password123");

        AuthResponse response = new AuthResponse("access", "refresh", "Pedro", "pedro@example.com");
        when(userService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access"));
    }

    @Test
    void register_emailAlreadyExists_throwsException() throws Exception {
        RegisterRequest dto = new RegisterRequest();
        dto.setName("Pedro");
        dto.setEmail("pedro@example.com");
        dto.setPassword("Password123");

        when(userService.register(any())).thenThrow(new RuntimeException("Email already registered"));

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    // --- POST /login ---

    @Test
    void login_validCredentials_returnsOk() throws Exception {
        LoginRequest dto = new LoginRequest();
        dto.setEmail("pedro@example.com");
        dto.setPassword("Password123");

        AuthResponse response = new AuthResponse("access", "refresh", "Pedro", "pedro@example.com");
        when(userService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"));
    }

    @Test
    void login_invalidCredentials_returnsUnauthorized() throws Exception {
        LoginRequest dto = new LoginRequest();
        dto.setEmail("pedro@example.com");
        dto.setPassword("wrongpassword");

        when(userService.login(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    // --- POST /refresh ---

    @Test
    void refresh_validToken_returnsOk() throws Exception {
        RefreshTokenRequest dto = new RefreshTokenRequest();
        dto.setRefreshToken("valid-refresh-token");

        AuthResponse response = new AuthResponse("new-access", "new-refresh", "Pedro", "pedro@example.com");
        when(userService.refresh(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"));
    }

    @Test
    void refresh_invalidToken_throwsException() throws Exception {
        RefreshTokenRequest dto = new RefreshTokenRequest();
        dto.setRefreshToken("invalid-token");

        when(userService.refresh(any())).thenThrow(new RuntimeException("Invalid refresh token"));

        mockMvc.perform(post("/api/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    // --- POST /logout ---

    @Test
    @WithMockUser
    void logout_authenticated_returnsNoContent() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .with(csrf())
                .header("Authorization", "Bearer some-token"))
                .andExpect(status().isNoContent());

        verify(userService).logout(eq("some-token"), any());
    }
}