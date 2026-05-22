package com.example.todolist.controller;

import com.example.todolist.dto.TaskPriorityStatistics;
import com.example.todolist.service.TaskStatisticsJdbcService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskStatisticsController {
    private final TaskStatisticsJdbcService taskStatisticsJdbcService;

    public TaskStatisticsController(TaskStatisticsJdbcService taskStatisticsJdbcService) {
        this.taskStatisticsJdbcService = taskStatisticsJdbcService;
    }

    @GetMapping("/tasks/statistics/priorities")
    public List<TaskPriorityStatistics> getTasksCountByPriority() {
        return taskStatisticsJdbcService.getTasksCountByPriority();
    }
}
