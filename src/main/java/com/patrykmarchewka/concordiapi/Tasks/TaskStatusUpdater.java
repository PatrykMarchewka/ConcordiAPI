package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.PublicVariables;

import java.time.OffsetDateTime;

public class TaskStatusUpdater implements TaskCREATEUpdater,TaskPUTUpdater,TaskPATCHUpdater{
    @Override
    public void CREATEUpdate(Task task, TaskRequestBody body) {
        task.setTaskStatus((body.getTaskStatus() == null ? PublicVariables.TaskStatus.NEW : body.getTaskStatus()));
    }

    @Override
    public void PUTUpdate(Task task, TaskRequestBody body) {
        task.setTaskStatus(body.getTaskStatus());
    }

    @Override
    public void PATCHUpdate(Task task, TaskRequestBody body) {
        if (body.getTaskStatus() != null){
            task.setTaskStatus(body.getTaskStatus());
        }
    }
}
