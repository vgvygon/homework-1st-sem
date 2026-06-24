package com.example.todolist.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("tasks")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldFindTasksByDueDateBetween() {
        LocalDate today = LocalDate.now();
        Task firstTask = task("First", today.plusDays(1));
        Task secondTask = task("Second", today.plusDays(3));
        Task thirdTask = task("Third", today.plusDays(10));
        taskRepository.save(firstTask);
        taskRepository.save(secondTask);
        taskRepository.save(thirdTask);

        var result = taskRepository.findTasksByDueDateBetween(today, today.plusDays(7));

        assertThat(result)
                .extracting(Task::getTitle)
                .containsExactly("First", "Second");
    }

    private Task task(String title, LocalDate dueDate) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("Description");
        task.setCompleted(false);
        task.setDueDate(dueDate);
        task.setPriority(Priority.MEDIUM);
        return task;
    }
}
