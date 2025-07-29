package com.patrykmarchewka.concordiapi.Tasks.Updaters.StatusUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPUTUpdater;

public class TaskStatusPUTUpdater implements TaskPUTUpdater {
    @Override
    public void PUTUpdate(Task task, TaskRequestBody body) {
        task.setTaskStatus(body.getTaskStatus());
    }
}
