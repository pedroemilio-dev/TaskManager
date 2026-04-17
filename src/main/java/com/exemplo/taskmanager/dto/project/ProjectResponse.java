package com.exemplo.taskmanager.dto.project;

import java.util.List;

import com.exemplo.taskmanager.dto.task.TaskResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String color;
    private Long parentId;
    private List<ProjectResponse> subProjects;
    private List<TaskResponse> tasks;
}