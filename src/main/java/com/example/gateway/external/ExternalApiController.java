package com.example.gateway.external;

import com.example.gateway.dto.TaskRequest;
import com.example.gateway.dto.TaskResponse;
import com.example.gateway.dto.UnstableResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
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
@RequestMapping("/external/v1")
public class ExternalApiController {
    private final Map<Long, TaskResponse> tasks = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        long id = sequence.getAndIncrement();
        TaskResponse task = new TaskResponse(id, request.title(), request.description(), request.completed());
        tasks.put(id, task);
        return ResponseEntity.created(URI.create("/external/v1/tasks/" + id)).body(task);
    }

    @GetMapping("/tasks/{id}")
    public TaskResponse findById(@PathVariable Long id) {
        TaskResponse task = tasks.get(id);
        if (task == null) {
            throw notFound(id);
        }
        return task;
    }

    @GetMapping("/tasks")
    public List<TaskResponse> findAll(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return tasks.values().stream()
                .filter(task -> completed == null || task.completed() == completed)
                .sorted(Comparator.comparing(TaskResponse::id))
                .limit(limit)
                .toList();
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        TaskResponse removed = tasks.remove(id);
        if (removed == null) {
            throw notFound(id);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public ResponseEntity<?> unstable(@RequestParam String mode) throws InterruptedException {
        return switch (mode) {
            case "timeout" -> {
                Thread.sleep(2000);
                yield ResponseEntity.ok(new UnstableResponse(mode, "response after timeout"));
            }
            case "500" -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(problem(HttpStatus.INTERNAL_SERVER_ERROR, "External error", "External service failed"));
            case "429" -> ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, "5")
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(problem(HttpStatus.TOO_MANY_REQUESTS, "Too many requests", "External rate limit exceeded"));
            case "html" -> ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .contentType(MediaType.TEXT_HTML)
                    .body("<html><body><h1>Bad Gateway</h1></body></html>");
            default -> ResponseEntity.ok(new UnstableResponse(mode, "ok"));
        };
    }

    private ExternalTaskNotFoundException notFound(Long id) {
        return new ExternalTaskNotFoundException(id);
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        return problemDetail;
    }
}
