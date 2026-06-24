package com.example.todolist.service;

import com.example.todolist.dto.TaskRequest;
import com.example.todolist.dto.TaskResponse;
import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public TaskResponse create(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setCompleted(Boolean.TRUE.equals(request.completed()));
        task.setDueDate(request.dueDate());
        task.setPriority(request.priority() == null ? Priority.MEDIUM : request.priority());
        return toResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        return taskRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Transactional
    public TaskResponse updateStatus(Long id, boolean completed) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.setCompleted(completed);
        return toResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findTasksDueBetween(java.time.LocalDate from, java.time.LocalDate to) {
        return taskRepository.findTasksByDueDateBetween(from, to).stream()
                .map(this::toResponse)
                .toList();
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getDueDate(),
                task.getPriority()
        );
    }
}
