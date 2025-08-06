package com.patrykmarchewka.concordiapi.Tasks.Updaters.UpdateDateUpdater;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPATCHUpdaterBasic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskUpdateDatePATCHUpdater implements TaskPATCHUpdaterBasic {

    private final TaskUpdateDateUpdaterHelper taskUpdateDateUpdaterHelper;

    @Autowired
    public TaskUpdateDatePATCHUpdater(TaskUpdateDateUpdaterHelper taskUpdateDateUpdaterHelper) {
        this.taskUpdateDateUpdaterHelper = taskUpdateDateUpdaterHelper;
    }

    /**
     * Sets the task update date to current time
     * @param task Task to edit
     */
    @Override
    public void PATCHUpdate(Task task) {
        taskUpdateDateUpdaterHelper.sharedUpdate(task);
    }
}
