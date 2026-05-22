package com.example.todolist.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TaskStatisticsJdbcServiceTest {
    @Autowired
    private TaskStatisticsJdbcService taskStatisticsJdbcService;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldReturnTasksCountByPriority() {
        Task task = new Task();
        task.setTitle("task");
        task.setPriority(Priority.HIGH);
        taskRepository.saveAndFlush(task);

        var result = taskStatisticsJdbcService.getTasksCountByPriority();

        assertThat(result).anySatisfy(statistics -> {
            assertThat(statistics.priority()).isEqualTo(Priority.HIGH);
            assertThat(statistics.count()).isEqualTo(1);
        });
    }
}
