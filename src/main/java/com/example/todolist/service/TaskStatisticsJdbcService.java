package com.example.todolist.service;

import com.example.todolist.dto.TaskPriorityStatistics;
import com.example.todolist.model.Priority;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class TaskStatisticsJdbcService {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<TaskPriorityStatistics> rowMapper = new TaskPriorityStatisticsRowMapper();

    public TaskStatisticsJdbcService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TaskPriorityStatistics> getTasksCountByPriority() {
        return jdbcTemplate.query(
                "select priority, count(*) as task_count from tasks group by priority order by priority",
                rowMapper
        );
    }

    private static class TaskPriorityStatisticsRowMapper implements RowMapper<TaskPriorityStatistics> {
        @Override
        public TaskPriorityStatistics mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return new TaskPriorityStatistics(
                    Priority.valueOf(resultSet.getString("priority")),
                    resultSet.getLong("task_count")
            );
        }
    }
}
