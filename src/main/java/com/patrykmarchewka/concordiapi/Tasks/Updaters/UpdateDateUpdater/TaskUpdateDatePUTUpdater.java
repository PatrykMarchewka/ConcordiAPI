package com.patrykmarchewka.concordiapi.Tasks.Updaters.UpdateDateUpdater;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPUTUpdaterBasic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskUpdateDatePUTUpdater implements TaskPUTUpdaterBasic {

    private final TaskUpdateDateUpdaterHelper taskUpdateDateUpdaterHelper;

    @Autowired
    public TaskUpdateDatePUTUpdater(TaskUpdateDateUpdaterHelper taskUpdateDateUpdaterHelper) {
        this.taskUpdateDateUpdaterHelper = taskUpdateDateUpdaterHelper;
    }

    @Override
    public void PUTUpdate(Task task) {
        taskUpdateDateUpdaterHelper.sharedUpdate(task);
    }
}
