package com.example.todolist.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskAttachment;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {
    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldFindByCompletedAndPriority() {
        Task task = new Task();
        task.setTitle("task");
        task.setCompleted(false);
        task.setPriority(Priority.HIGH);
        task.setDueDate(LocalDate.now().plusDays(1));
        taskRepository.save(task);

        List<Task> result = taskRepository.findByCompletedAndPriority(false, Priority.HIGH);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("task");
    }

    @Test
    void shouldFindTasksDueInNextSevenDays() {
        Task firstTask = new Task();
        firstTask.setTitle("soon");
        firstTask.setPriority(Priority.MEDIUM);
        firstTask.setDueDate(LocalDate.now().plusDays(3));

        Task secondTask = new Task();
        secondTask.setTitle("later");
        secondTask.setPriority(Priority.MEDIUM);
        secondTask.setDueDate(LocalDate.now().plusDays(10));

        taskRepository.save(firstTask);
        taskRepository.save(secondTask);

        List<Task> result = taskRepository.findTasksDueInNextSevenDays(LocalDate.now(), LocalDate.now().plusDays(7));

        assertThat(result).extracting(Task::getTitle).containsExactly("soon");
    }

    @Test
    void shouldSaveTaskWithAttachment() {
        Task task = new Task();
        task.setTitle("task with file");
        task.setPriority(Priority.LOW);

        TaskAttachment attachment = new TaskAttachment();
        attachment.setFileName("file.txt");
        attachment.setFilePath("/tmp/file.txt");
        attachment.setContentType("text/plain");
        attachment.setFileSize(10);
        task.addAttachment(attachment);

        Task savedTask = taskRepository.saveAndFlush(task);

        List<Task> tasks = taskRepository.findAllWithAttachments();

        assertThat(savedTask.getId()).isNotNull();
        assertThat(tasks).hasSize(1);
        assertThat(tasks.getFirst().getAttachments()).hasSize(1);
    }

    @Test
    void shouldDeleteAttachmentsWhenTaskDeleted() {
        Task task = new Task();
        task.setTitle("task");
        task.setPriority(Priority.HIGH);

        TaskAttachment attachment = new TaskAttachment();
        attachment.setFileName("file.txt");
        attachment.setFilePath("/tmp/file.txt");
        attachment.setFileSize(1);
        task.addAttachment(attachment);

        Task savedTask = taskRepository.saveAndFlush(task);
        taskRepository.deleteById(savedTask.getId());
        taskRepository.flush();

        assertThat(taskRepository.findAllWithAttachments()).isEmpty();
    }
}
