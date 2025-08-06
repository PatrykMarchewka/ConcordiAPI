package com.patrykmarchewka.concordiapi.Tasks.Updaters.NameUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPUTUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskNamePUTUpdater implements TaskPUTUpdater {

    private final TaskNameUpdaterHelper taskNameUpdaterHelper;

    @Autowired
    public TaskNamePUTUpdater(TaskNameUpdaterHelper taskNameUpdaterHelper){
        this.taskNameUpdaterHelper = taskNameUpdaterHelper;
    }

    @Override
    public void PUTUpdate(Task task, TaskRequestBody body) {
        taskNameUpdaterHelper.sharedUpdate(task, body);
    }
}
