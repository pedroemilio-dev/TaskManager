package com.exemplo.taskmanager.dto.user;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String email;
}
