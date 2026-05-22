package com.example.todolist.dto;

import com.example.todolist.model.Priority;

public record TaskPriorityStatistics(
        Priority priority,
        long count
) {
}
