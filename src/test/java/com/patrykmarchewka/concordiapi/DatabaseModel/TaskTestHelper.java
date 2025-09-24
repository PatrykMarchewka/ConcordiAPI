package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.OffsetDateTimeConverter;
import com.patrykmarchewka.concordiapi.TaskStatus;

public interface TaskTestHelper {
    default Task createTask(Team team,TaskRepository taskRepository){
        Task task = new Task();
        task.setName("TEST TASK");
        task.setDescription("Description");
        task.setTaskStatus(TaskStatus.NEW);
        task.setAssignedTeam(team);
        task.setCreationDate(OffsetDateTimeConverter.nowConverted().minusSeconds(2));
        task.setUpdateDate(OffsetDateTimeConverter.nowConverted().minusSeconds(1));
        return taskRepository.save(task);
    }
}
