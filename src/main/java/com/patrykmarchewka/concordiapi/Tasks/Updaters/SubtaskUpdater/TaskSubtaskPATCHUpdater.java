package com.patrykmarchewka.concordiapi.Tasks.Updaters.SubtaskUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPATCHUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskSubtaskPATCHUpdater implements TaskPATCHUpdater {

    private final TaskSubtaskUpdaterHelper taskSubtaskUpdaterHelper;

    @Autowired
    public TaskSubtaskPATCHUpdater(TaskSubtaskUpdaterHelper taskSubtaskUpdaterHelper){
        this.taskSubtaskUpdaterHelper = taskSubtaskUpdaterHelper;
    }

    @Override
    public void PATCHUpdate(Task task, TaskRequestBody body) {
        taskSubtaskUpdaterHelper.sharedUpdate(task, body);
    }
}
