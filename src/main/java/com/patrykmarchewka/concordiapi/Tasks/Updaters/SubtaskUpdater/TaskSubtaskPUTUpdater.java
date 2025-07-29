package com.patrykmarchewka.concordiapi.Tasks.Updaters.SubtaskUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPUTUpdater;

public class TaskSubtaskPUTUpdater implements TaskPUTUpdater {

    private final TaskSubtaskUpdaterHelper taskSubtaskUpdaterHelper;

    public TaskSubtaskPUTUpdater(TaskSubtaskUpdaterHelper taskSubtaskUpdaterHelper){
        this.taskSubtaskUpdaterHelper = taskSubtaskUpdaterHelper;
    }

    @Override
    public void PUTUpdate(Task task, TaskRequestBody body) {
        taskSubtaskUpdaterHelper.sharedUpdate(task, body);
    }
}
