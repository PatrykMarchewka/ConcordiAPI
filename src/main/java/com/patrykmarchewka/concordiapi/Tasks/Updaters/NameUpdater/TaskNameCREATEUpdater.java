package com.patrykmarchewka.concordiapi.Tasks.Updaters.NameUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskCREATEUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskNameCREATEUpdater implements TaskCREATEUpdater {

    private final TaskNameUpdaterHelper taskNameUpdaterHelper;

    @Autowired
    public TaskNameCREATEUpdater(TaskNameUpdaterHelper taskNameUpdaterHelper){
        this.taskNameUpdaterHelper = taskNameUpdaterHelper;
    }

    @Override
    public void CREATEUpdate(Task task, TaskRequestBody body) {
        taskNameUpdaterHelper.sharedUpdate(task, body);
    }
}
