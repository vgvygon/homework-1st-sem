package com.example.todolist.mapper;

import com.example.todolist.dto.TaskAttachmentResponse;
import com.example.todolist.dto.TaskRequest;
import com.example.todolist.dto.TaskResponse;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskAttachment;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "tags", expression = "java(joinTags(request.tags()))")
    Task toEntity(TaskRequest request);

    @Mapping(target = "tags", expression = "java(splitTags(task.getTags()))")
    TaskResponse toResponse(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "tags", expression = "java(joinTags(request.tags()))")
    void updateEntity(TaskRequest request, @MappingTarget Task task);

    @Mapping(target = "taskId", expression = "java(attachment.getTask().getId())")
    TaskAttachmentResponse toAttachmentResponse(TaskAttachment attachment);

    default String joinTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return String.join(",", tags);
    }

    default List<String> splitTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }
}
