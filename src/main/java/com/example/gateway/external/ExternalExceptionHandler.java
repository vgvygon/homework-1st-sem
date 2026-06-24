package com.example.gateway.external;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = ExternalApiController.class)
public class ExternalExceptionHandler {
    @ExceptionHandler(ExternalTaskNotFoundException.class)
    public ProblemDetail handleNotFound(ExternalTaskNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problemDetail.setTitle("Task not found");
        problemDetail.setProperty("taskId", exception.getId());
        return problemDetail;
    }
}
