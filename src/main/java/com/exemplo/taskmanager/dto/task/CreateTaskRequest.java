package com.exemplo.taskmanager.dto.task;

import java.time.LocalDate;

import com.exemplo.taskmanager.model.Task.Priority;

import lombok.Data;

@Data
public class CreateTaskRequest {
    private String name;
    private String description;
    private LocalDate dueDate;
    private Priority priority;
    private Long projectId;
}