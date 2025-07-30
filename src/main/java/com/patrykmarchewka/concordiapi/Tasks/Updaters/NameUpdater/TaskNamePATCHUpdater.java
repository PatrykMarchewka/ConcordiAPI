package com.patrykmarchewka.concordiapi.Tasks.Updaters.NameUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPATCHUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskNamePATCHUpdater implements TaskPATCHUpdater {

    private final TaskNameUpdaterHelper taskNameUpdaterHelper;

    @Autowired
    public TaskNamePATCHUpdater(TaskNameUpdaterHelper taskNameUpdaterHelper){
        this.taskNameUpdaterHelper = taskNameUpdaterHelper;
    }

    @Override
    public void PATCHUpdate(Task task, TaskRequestBody body) {

        if (body.getName() != null) {
            taskNameUpdaterHelper.sharedUpdate(task, body);
        }
    }
}
