package com.patrykmarchewka.concordiapi.Tasks.Updaters.NameUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;

public class TaskNameUpdaterHelper {

    void sharedUpdate(Task task, TaskRequestBody body){
        task.setName(body.getName());
    }
}
