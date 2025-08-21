package com.patrykmarchewka.concordiapi.Tasks.Updaters.StatusUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPATCHUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusPATCHUpdater implements TaskPATCHUpdater {

    private final TaskStatusUpdaterHelper taskStatusUpdaterHelper;

    @Autowired
    public TaskStatusPATCHUpdater(TaskStatusUpdaterHelper taskStatusUpdaterHelper) {
        this.taskStatusUpdaterHelper = taskStatusUpdaterHelper;
    }

    @Override
    public void PATCHUpdate(Task task, TaskRequestBody body) {
        if (body.getTaskStatus() != null){
            taskStatusUpdaterHelper.sharedUpdate(task, body);
        }
    }
}
