package com.patrykmarchewka.concordiapi.Tasks.Updaters.StatusUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPUTUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusPUTUpdater implements TaskPUTUpdater {

    private final TaskStatusUpdaterHelper taskStatusUpdaterHelper;

    @Autowired
    public TaskStatusPUTUpdater(TaskStatusUpdaterHelper taskStatusUpdaterHelper) {
        this.taskStatusUpdaterHelper = taskStatusUpdaterHelper;
    }

    @Override
    public void PUTUpdate(Task task, TaskRequestBody body) {
        taskStatusUpdaterHelper.sharedUpdate(task, body);
    }
}
