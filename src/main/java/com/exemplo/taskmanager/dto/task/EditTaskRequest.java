package com.exemplo.taskmanager.dto.task;

import com.exemplo.taskmanager.model.Task.Priority;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EditTaskRequest {
    private String name;
    private String description;
    private LocalDateTime dueDate;
    private Priority priority;
    private Long projectId;
}