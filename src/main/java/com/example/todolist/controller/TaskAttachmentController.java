package com.example.todolist.controller;

import com.example.todolist.dto.TaskAttachmentRequest;
import com.example.todolist.dto.TaskAttachmentResponse;
import com.example.todolist.service.AttachmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks/{taskId}/attachments")
public class TaskAttachmentController {
    private final AttachmentService attachmentService;

    public TaskAttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping
    public List<TaskAttachmentResponse> findByTaskId(@PathVariable Long taskId) {
        return attachmentService.findByTaskId(taskId);
    }

    @PostMapping
    public TaskAttachmentResponse create(@PathVariable Long taskId, @Valid @RequestBody TaskAttachmentRequest request) {
        return attachmentService.create(taskId, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        attachmentService.delete(id);
    }
}
