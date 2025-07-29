package com.patrykmarchewka.concordiapi.Tasks.Updaters.SubtaskUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskCREATEUpdater;


public class TaskSubtaskCREATEUpdater implements TaskCREATEUpdater {

    private final TaskSubtaskUpdaterHelper taskSubtaskUpdaterHelper;

    public TaskSubtaskCREATEUpdater(TaskSubtaskUpdaterHelper taskSubtaskUpdaterHelper){
        this.taskSubtaskUpdaterHelper = taskSubtaskUpdaterHelper;
    }

    @Override
    public void CREATEUpdate(Task task, TaskRequestBody body) {
        taskSubtaskUpdaterHelper.sharedUpdate(task, body);
    }
}
