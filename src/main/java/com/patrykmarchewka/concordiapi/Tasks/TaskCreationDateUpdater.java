package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;

import java.time.OffsetDateTime;

public class TaskCreationDateUpdater implements  TaskCREATEUpdater{
    @Override
    public void CREATEUpdate(Task task, TaskRequestBody body) {
        task.setCreationDate(OffsetDateTime.now());
    }
}
