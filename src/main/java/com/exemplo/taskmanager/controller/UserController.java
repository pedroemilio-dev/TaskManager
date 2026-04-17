package com.exemplo.taskmanager.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.exemplo.taskmanager.dto.user.ChangePasswordRequest;
import com.exemplo.taskmanager.dto.user.UpdateUserRequest;
import com.exemplo.taskmanager.dto.user.UserResponse;
import com.exemplo.taskmanager.model.User;
import com.exemplo.taskmanager.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getProfile(@AuthenticationPrincipal User user) {
        return userService.getProfile(user);
    }

    @PatchMapping("/me")
    public UserResponse updateProfile(@AuthenticationPrincipal User user,
                                      @RequestBody UpdateUserRequest request) {
        return userService.updateProfile(user, request);
    }

    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal User user,
                               @RequestBody ChangePasswordRequest request) {
        userService.changePassword(user, request);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@AuthenticationPrincipal User user,
                              HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        userService.deleteAccount(user, token);
    }
}