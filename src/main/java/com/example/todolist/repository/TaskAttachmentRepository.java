package com.example.todolist.repository;

import com.example.todolist.model.TaskAttachment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {
    List<TaskAttachment> findByTaskId(Long taskId);
}
