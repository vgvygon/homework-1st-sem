package com.example.gateway.client;

import com.example.gateway.dto.TaskCreatedResponse;
import com.example.gateway.dto.TaskRequest;
import com.example.gateway.dto.TaskResponse;
import com.example.gateway.dto.UnstableResponse;
import com.example.gateway.exception.ExternalApiException;
import com.example.gateway.exception.ExternalTimeoutException;
import com.example.gateway.exception.TaskNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class ExternalTasksClient {
    private static final Logger log = LoggerFactory.getLogger(ExternalTasksClient.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ExternalTasksClient(RestClient externalRestClient, ObjectMapper objectMapper) {
        this.restClient = externalRestClient;
        this.objectMapper = objectMapper;
    }

    public TaskCreatedResponse create(TaskRequest request) {
        return execute(() -> restClient.post()
                .uri("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange((clientRequest, response) -> {
                    String body = readBody(response);
                    HttpStatusCode status = response.getStatusCode();
                    if (status.value() == 201) {
                        ensureJson(response.getHeaders(), body);
                        URI location = response.getHeaders().getLocation();
                        TaskResponse task = objectMapper.readValue(body, TaskResponse.class);
                        return new TaskCreatedResponse(task, location);
                    }
                    handleError(status, response.getHeaders(), body);
                    throw new ExternalApiException("Unexpected external API response: " + status.value());
                }));
    }

    public TaskResponse findById(Long id) {
        return execute(() -> restClient.get()
                .uri("/tasks/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange((request, response) -> {
                    String body = readBody(response);
                    HttpStatusCode status = response.getStatusCode();
                    if (status.is2xxSuccessful()) {
                        ensureJson(response.getHeaders(), body);
                        return objectMapper.readValue(body, TaskResponse.class);
                    }
                    handleError(status, response.getHeaders(), body);
                    throw new ExternalApiException("Unexpected external API response: " + status.value());
                }));
    }

    public List<TaskResponse> findAll(Boolean completed, int limit) {
        return execute(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tasks")
                        .queryParamIfPresent("completed", java.util.Optional.ofNullable(completed))
                        .queryParam("limit", limit)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    String body = readBody(response);
                    handleError(response.getStatusCode(), response.getHeaders(), body);
                })
                .body(new ParameterizedTypeReference<List<TaskResponse>>() {
                }));
    }

    public void delete(Long id) {
        execute(() -> {
            restClient.delete()
                    .uri("/tasks/{id}", id)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        String body = readBody(response);
                        handleError(response.getStatusCode(), response.getHeaders(), body);
                    })
                    .toBodilessEntity();
            return null;
        });
    }

    public UnstableResponse unstable(String mode) {
        return execute(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/unstable").queryParam("mode", mode).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange((request, response) -> {
                    String body = readBody(response);
                    HttpStatusCode status = response.getStatusCode();
                    if (status.is2xxSuccessful()) {
                        ensureJson(response.getHeaders(), body);
                        return objectMapper.readValue(body, UnstableResponse.class);
                    }
                    handleError(status, response.getHeaders(), body);
                    throw new ExternalApiException("Unexpected external API response: " + status.value());
                }));
    }

    private <T> T execute(ClientCall<T> call) {
        try {
            return call.execute();
        } catch (ResourceAccessException exception) {
            if (hasTimeout(exception)) {
                throw new ExternalTimeoutException("External API timeout", exception);
            }
            throw exception;
        }
    }

    private boolean hasTimeout(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SocketTimeoutException || current instanceof java.net.http.HttpTimeoutException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private void handleError(HttpStatusCode status, HttpHeaders headers, String body) throws IOException {
        if (status.value() == 404) {
            ProblemDetail problemDetail = parseProblemDetail(headers, body);
            throw new TaskNotFoundException(problemDetail.getDetail());
        }

        if (status.is5xxServerError() || status.value() == 429) {
            if (!isJson(headers)) {
                log.warn("External API returned non-json content-type={} body={}", headers.getContentType(), limitBody(body));
            }
            throw new ExternalApiException("External API failed with status " + status.value());
        }

        throw new ExternalApiException("External API returned status " + status.value());
    }

    private ProblemDetail parseProblemDetail(HttpHeaders headers, String body) throws IOException {
        ensureJson(headers, body);
        return objectMapper.readValue(body, ProblemDetail.class);
    }

    private void ensureJson(HttpHeaders headers, String body) {
        if (!isJson(headers)) {
            log.warn("Unexpected external content-type={} body={}", headers.getContentType(), limitBody(body));
            throw new ExternalApiException("External API returned unexpected content type");
        }
    }

    private boolean isJson(HttpHeaders headers) {
        MediaType contentType = headers.getContentType();
        return contentType != null && (MediaType.APPLICATION_JSON.includes(contentType) || MediaType.APPLICATION_PROBLEM_JSON.includes(contentType));
    }

    private String readBody(org.springframework.http.client.ClientHttpResponse response) throws IOException {
        return StreamUtils.copyToString(response.getBody(), java.nio.charset.StandardCharsets.UTF_8);
    }

    private String limitBody(String body) {
        if (body == null) {
            return "";
        }
        return body.length() <= 300 ? body : body.substring(0, 300);
    }

    @FunctionalInterface
    private interface ClientCall<T> {
        T execute();
    }
}
