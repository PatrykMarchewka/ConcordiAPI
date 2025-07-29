package com.patrykmarchewka.concordiapi.Tasks.Updaters.DescriptionUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPUTUpdater;

public class TaskDescriptionPUTUpdater implements TaskPUTUpdater {
    @Override
    public void PUTUpdate(Task task, TaskRequestBody body) {
        task.setDescription(body.getDescription());
    }
}
