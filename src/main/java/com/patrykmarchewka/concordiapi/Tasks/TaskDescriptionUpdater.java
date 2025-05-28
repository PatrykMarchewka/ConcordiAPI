package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;

public class TaskDescriptionUpdater implements TaskCREATEUpdater,TaskPUTUpdater,TaskPATCHUpdater{
    @Override
    public void CREATEUpdate(Task task, TaskRequestBody body) {
        task.setDescription((body.getDescription() == null ? null : body.getDescription()));
    }

    @Override
    public void PUTUpdate(Task task, TaskRequestBody body) {
        task.setDescription(body.getDescription());
    }

    @Override
    public void PATCHUpdate(Task task, TaskRequestBody body) {
        if (body.getDescription() != null){
            task.setDescription(body.getDescription());
        }
    }
}
