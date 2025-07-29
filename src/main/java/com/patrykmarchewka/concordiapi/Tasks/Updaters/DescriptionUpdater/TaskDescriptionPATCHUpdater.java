package com.patrykmarchewka.concordiapi.Tasks.Updaters.DescriptionUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPATCHUpdater;

public class TaskDescriptionPATCHUpdater implements TaskPATCHUpdater {
    @Override
    public void PATCHUpdate(Task task, TaskRequestBody body) {
        if (body.getDescription() != null){
            task.setDescription(body.getDescription());
        }
    }
}
