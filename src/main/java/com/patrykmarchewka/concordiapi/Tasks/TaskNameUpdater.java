package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;

public class TaskNameUpdater implements TaskCREATEUpdater,TaskPUTUpdater,TaskPATCHUpdater{
    @Override
    public void CREATEUpdate(Task task, TaskRequestBody body) {
        sharedUpdate(task, body);
    }

    @Override
    public void PUTUpdate(Task task, TaskRequestBody body) {
        sharedUpdate(task,body);
    }

    @Override
    public void PATCHUpdate(Task task, TaskRequestBody body){
        if (body.getName() != null){
            sharedUpdate(task, body);
        }
    }

    private void sharedUpdate(Task task, TaskRequestBody body){
        task.setName(body.getName());
    }
}
