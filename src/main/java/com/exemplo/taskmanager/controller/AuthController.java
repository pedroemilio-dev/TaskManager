package com.exemplo.taskmanager.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.exemplo.taskmanager.dto.auth.AuthResponse;
import com.exemplo.taskmanager.dto.auth.LoginRequest;
import com.exemplo.taskmanager.dto.auth.RefreshTokenRequest;
import com.exemplo.taskmanager.dto.auth.RegisterRequest;
import com.exemplo.taskmanager.model.User;
import com.exemplo.taskmanager.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshTokenRequest request) {
        return userService.refresh(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request,
                       @AuthenticationPrincipal User user) {
        String token = request.getHeader("Authorization").substring(7);
        userService.logout(token, user);
    }
}