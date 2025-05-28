package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;

import java.time.OffsetDateTime;

public class TaskUpdateDateUpdater implements  TaskCREATEUpdater,TaskPUTUpdater,TaskPATCHUpdater{
    @Override
    public void CREATEUpdate(Task task, TaskRequestBody body) {
        sharedUpdate(task);
    }

    @Override
    public void PUTUpdate(Task task, TaskRequestBody body) {
        sharedUpdate(task);
    }

    @Override
    public void PATCHUpdate(Task task, TaskRequestBody body) {
        sharedUpdate(task);

    }

    private void sharedUpdate(Task task){
        task.setUpdateDate(OffsetDateTime.now());
    }
}
