package com.example.todolist.controller;

import com.example.todolist.dto.TaskRequest;
import com.example.todolist.dto.TaskResponse;
import com.example.todolist.model.Priority;
import com.example.todolist.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponse> findAll() {
        return taskService.findAll();
    }

    @GetMapping("/with-attachments")
    public List<TaskResponse> findAllWithAttachments() {
        return taskService.findAllWithAttachments();
    }

    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable Long id) {
        return taskService.findById(id);
    }

    @GetMapping("/filter")
    public List<TaskResponse> findByCompletedAndPriority(@RequestParam boolean completed, @RequestParam Priority priority) {
        return taskService.findByCompletedAndPriority(completed, priority);
    }

    @GetMapping("/due-soon")
    public List<TaskResponse> findDueInNextSevenDays() {
        return taskService.findDueInNextSevenDays();
    }

    @PostMapping
    public TaskResponse create(@Valid @RequestBody TaskRequest request) {
        return taskService.create(request);
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return taskService.update(id, request);
    }

    @PatchMapping("/complete")
    public void bulkComplete(@RequestBody List<Long> ids) {
        taskService.bulkCompleteTasks(ids);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}
