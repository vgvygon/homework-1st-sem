package com.example.todolist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class TaskServiceTest {
    @Autowired
    private TaskService taskService;

    @MockitoBean
    private TaskRepository taskRepository;

    @Test
    void shouldUpdateStatusOfExistingTask() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Task");
        task.setDescription("Description");
        task.setCompleted(false);
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setPriority(Priority.HIGH);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = taskService.updateStatus(1L, true);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(captor.capture());
        assertThat(response.completed()).isTrue();
        assertThat(captor.getValue().isCompleted()).isTrue();
    }
}
