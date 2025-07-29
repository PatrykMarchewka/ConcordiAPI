package com.patrykmarchewka.concordiapi.Tasks.Updaters.SubtaskUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPATCHUpdater;

public class TaskSubtaskPATCHUpdater implements TaskPATCHUpdater {

    private final TaskSubtaskUpdaterHelper taskSubtaskUpdaterHelper;

    public TaskSubtaskPATCHUpdater(TaskSubtaskUpdaterHelper taskSubtaskUpdaterHelper){
        this.taskSubtaskUpdaterHelper = taskSubtaskUpdaterHelper;
    }

    @Override
    public void PATCHUpdate(Task task, TaskRequestBody body) {
        taskSubtaskUpdaterHelper.sharedUpdate(task, body);
    }
}
