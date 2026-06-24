package com.example.todolist.repository;

import com.example.todolist.model.Task;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("select task from Task task where task.dueDate between :from and :to order by task.dueDate")
    List<Task> findTasksByDueDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
