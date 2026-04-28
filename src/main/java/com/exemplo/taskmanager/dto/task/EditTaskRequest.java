package com.exemplo.taskmanager.dto.task;

import com.exemplo.taskmanager.model.Task.Priority;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EditTaskRequest {
    private String name;
    private String description;
    private LocalDate dueDate;
    private Priority priority;
    private Long projectId;
}