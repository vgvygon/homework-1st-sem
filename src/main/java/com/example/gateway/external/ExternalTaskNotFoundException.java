package com.example.gateway.external;

public class ExternalTaskNotFoundException extends RuntimeException {
    private final Long id;

    public ExternalTaskNotFoundException(Long id) {
        super("Task with id " + id + " not found");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
