package com.patrykmarchewka.concordiapi.Tasks.Updaters.StatusUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPATCHUpdater;

public class TaskStatusPATCHUpdater implements TaskPATCHUpdater {
    @Override
    public void PATCHUpdate(Task task, TaskRequestBody body) {
        if (body.getTaskStatus() != null){
            task.setTaskStatus(body.getTaskStatus());
        }
    }
}
