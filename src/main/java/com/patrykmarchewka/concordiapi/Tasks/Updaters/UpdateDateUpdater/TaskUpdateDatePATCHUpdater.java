package com.patrykmarchewka.concordiapi.Tasks.Updaters.UpdateDateUpdater;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPATCHUpdaterBasic;

public class TaskUpdateDatePATCHUpdater implements TaskPATCHUpdaterBasic {

    private final TaskUpdateDateUpdaterHelper taskUpdateDateUpdaterHelper;

    public TaskUpdateDatePATCHUpdater(TaskUpdateDateUpdaterHelper taskUpdateDateUpdaterHelper) {
        this.taskUpdateDateUpdaterHelper = taskUpdateDateUpdaterHelper;
    }

    @Override
    public void PATCHUpdate(Task task) {
        taskUpdateDateUpdaterHelper.sharedUpdate(task);
    }
}
