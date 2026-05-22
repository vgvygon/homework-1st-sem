package com.example.todolist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.todolist.dto.TaskRequest;
import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceTest {
    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldRollbackBulkCompleteTasks() {
        Task firstTask = createTask("first");
        Task secondTask = createTask("second");

        assertThatThrownBy(() -> taskService.bulkCompleteTasks(List.of(firstTask.getId(), 999999L, secondTask.getId())))
                .isInstanceOf(TaskNotFoundException.class);

        assertThat(taskRepository.findById(firstTask.getId()).orElseThrow().isCompleted()).isFalse();
        assertThat(taskRepository.findById(secondTask.getId()).orElseThrow().isCompleted()).isFalse();
    }

    @Test
    void shouldCreateTask() {
        TaskRequest request = new TaskRequest(
                "created",
                "description",
                false,
                Priority.HIGH,
                LocalDate.now().plusDays(1),
                List.of("java", "jpa")
        );

        var response = taskService.create(request);

        assertThat(response.id()).isNotNull();
        assertThat(response.tags()).containsExactly("java", "jpa");
    }

    private Task createTask(String title) {
        Task task = new Task();
        task.setTitle(title);
        task.setPriority(Priority.MEDIUM);
        task.setCompleted(false);
        return taskRepository.saveAndFlush(task);
    }
}
