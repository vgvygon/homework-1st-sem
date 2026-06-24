package com.example.gateway.service;

import com.example.gateway.client.ExternalTasksClient;
import com.example.gateway.dto.TaskCreatedResponse;
import com.example.gateway.dto.TaskRequest;
import com.example.gateway.dto.TaskResponse;
import com.example.gateway.dto.UnstableResponse;
import com.example.gateway.exception.TaskNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.net.URI;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TasksGatewayService {
    private final ExternalTasksClient externalTasksClient;

    public TasksGatewayService(ExternalTasksClient externalTasksClient) {
        this.externalTasksClient = externalTasksClient;
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "createFallback")
    public TaskCreatedResponse create(TaskRequest request) {
        return externalTasksClient.create(request);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "findByIdFallback")
    public TaskResponse findById(Long id) {
        return externalTasksClient.findById(id);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "findAllFallback")
    public List<TaskResponse> findAll(Boolean completed, int limit) {
        return externalTasksClient.findAll(completed, limit);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "deleteFallback")
    public void delete(Long id) {
        externalTasksClient.delete(id);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "unstableFallback")
    public UnstableResponse unstable(String mode) {
        return externalTasksClient.unstable(mode);
    }

    public TaskCreatedResponse createFallback(TaskRequest request, Throwable throwable) {
        rethrowRateLimit(throwable);
        rethrowNotFound(throwable);
        TaskResponse task = new TaskResponse(-1L, request.title(), "fallback: external service is unavailable", request.completed());
        return new TaskCreatedResponse(task, URI.create("/api/v1/tasks/-1"));
    }

    public TaskResponse findByIdFallback(Long id, Throwable throwable) {
        rethrowRateLimit(throwable);
        rethrowNotFound(throwable);
        return new TaskResponse(id, "fallback task", "external service is unavailable", false);
    }

    public List<TaskResponse> findAllFallback(Boolean completed, int limit, Throwable throwable) {
        rethrowRateLimit(throwable);
        rethrowNotFound(throwable);
        return List.of(new TaskResponse(-1L, "fallback task", "external service is unavailable", completed != null && completed));
    }

    public void deleteFallback(Long id, Throwable throwable) {
        rethrowRateLimit(throwable);
        rethrowNotFound(throwable);
    }

    public UnstableResponse unstableFallback(String mode, Throwable throwable) {
        rethrowRateLimit(throwable);
        if (throwable instanceof CallNotPermittedException) {
            return new UnstableResponse(mode, "circuit breaker is open, fallback response returned");
        }
        return new UnstableResponse(mode, "external service failed, fallback response returned");
    }

    private void rethrowRateLimit(Throwable throwable) {
        if (throwable instanceof RequestNotPermitted) {
            throw (RequestNotPermitted) throwable;
        }
    }

    private void rethrowNotFound(Throwable throwable) {
        if (throwable instanceof TaskNotFoundException) {
            throw (TaskNotFoundException) throwable;
        }
    }
}
