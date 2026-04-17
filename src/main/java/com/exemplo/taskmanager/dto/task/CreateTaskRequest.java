package com.exemplo.taskmanager.dto.task;

import java.time.LocalDateTime;

import com.exemplo.taskmanager.model.Task.Priority;

import lombok.Data;

@Data
public class CreateTaskRequest {
    private String name;
    private String description;
    private LocalDateTime dueDate;
    private Priority priority;
    private Long projectId;
}