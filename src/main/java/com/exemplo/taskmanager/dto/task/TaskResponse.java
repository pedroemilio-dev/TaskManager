package com.exemplo.taskmanager.dto.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.exemplo.taskmanager.model.Task.Priority;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String name;
    private String description;
    private boolean completed;
    private Priority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
}