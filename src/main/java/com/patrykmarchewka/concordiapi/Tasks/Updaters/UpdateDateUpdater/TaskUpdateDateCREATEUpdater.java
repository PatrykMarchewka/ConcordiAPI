package com.patrykmarchewka.concordiapi.Tasks.Updaters.UpdateDateUpdater;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskCREATEUpdaterBasic;

public class TaskUpdateDateCREATEUpdater implements TaskCREATEUpdaterBasic {

    private final TaskUpdateDateUpdaterHelper taskUpdateDateUpdaterHelper;

    public TaskUpdateDateCREATEUpdater(TaskUpdateDateUpdaterHelper taskUpdateDateUpdaterHelper) {
        this.taskUpdateDateUpdaterHelper = taskUpdateDateUpdaterHelper;
    }

    /**
     * Sets the task update date to now
     * @param task Task to edit
     */
    @Override
    public void CREATEUpdate(Task task) {
        taskUpdateDateUpdaterHelper.sharedUpdate(task);
    }
}
