package com.exemplo.taskmanager.dto.project;

import lombok.Data;

@Data
public class CreateProjectRequest {
    private String name;
    private String color;
    private Long parentId;
}