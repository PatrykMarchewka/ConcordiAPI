package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.TaskStatus;

import java.time.OffsetDateTime;

public interface TaskTestHelper {
    default Task createTask(Team team,TaskRepository taskRepository){
        Task task = new Task();
        task.setName("TEST TASK");
        task.setDescription("Description");
        task.setTaskStatus(TaskStatus.NEW);
        task.setAssignedTeam(team);
        task.setCreationDate(OffsetDateTime.now().minusSeconds(2));
        task.setUpdateDate(OffsetDateTime.now().minusSeconds(1));
        return taskRepository.save(task);
    }
}
