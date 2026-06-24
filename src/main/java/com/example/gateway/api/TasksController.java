package com.example.gateway.api;

import com.example.gateway.dto.TaskCreatedResponse;
import com.example.gateway.dto.TaskRequest;
import com.example.gateway.dto.TaskResponse;
import com.example.gateway.dto.UnstableResponse;
import com.example.gateway.service.TasksGatewayService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TasksController {
    private final TasksGatewayService tasksGatewayService;

    public TasksController(TasksGatewayService tasksGatewayService) {
        this.tasksGatewayService = tasksGatewayService;
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskCreatedResponse> create(@Valid @RequestBody TaskRequest request) {
        TaskCreatedResponse response = tasksGatewayService.create(request);
        return ResponseEntity.created(response.location()).body(response);
    }

    @GetMapping("/tasks/{id}")
    public TaskResponse findById(@PathVariable Long id) {
        return tasksGatewayService.findById(id);
    }

    @GetMapping("/tasks")
    public List<TaskResponse> findAll(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return tasksGatewayService.findAll(completed, limit);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tasksGatewayService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public UnstableResponse unstable(@RequestParam String mode) {
        return tasksGatewayService.unstable(mode);
    }
}
