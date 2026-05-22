package com.example.todolist.service;

import com.example.todolist.dto.TaskAttachmentRequest;
import com.example.todolist.dto.TaskAttachmentResponse;
import com.example.todolist.exception.AttachmentNotFoundException;
import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.repository.TaskAttachmentRepository;
import com.example.todolist.repository.TaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttachmentService {
    private final TaskRepository taskRepository;
    private final TaskAttachmentRepository attachmentRepository;
    private final TaskMapper taskMapper;

    public AttachmentService(TaskRepository taskRepository, TaskAttachmentRepository attachmentRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.attachmentRepository = attachmentRepository;
        this.taskMapper = taskMapper;
    }

    @Transactional(readOnly = true)
    public List<TaskAttachmentResponse> findByTaskId(Long taskId) {
        return attachmentRepository.findByTaskId(taskId).stream()
                .map(taskMapper::toAttachmentResponse)
                .toList();
    }

    @Transactional
    public TaskAttachmentResponse create(Long taskId, TaskAttachmentRequest request) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(task);
        attachment.setFileName(request.fileName());
        attachment.setFilePath(request.filePath());
        attachment.setContentType(request.contentType());
        attachment.setFileSize(request.fileSize());
        TaskAttachment savedAttachment = attachmentRepository.save(attachment);
        return taskMapper.toAttachmentResponse(savedAttachment);
    }

    @Transactional
    public void delete(Long id) {
        if (!attachmentRepository.existsById(id)) {
            throw new AttachmentNotFoundException(id);
        }
        attachmentRepository.deleteById(id);
    }
}
