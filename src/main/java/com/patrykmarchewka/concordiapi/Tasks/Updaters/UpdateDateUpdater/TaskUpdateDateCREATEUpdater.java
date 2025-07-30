package com.patrykmarchewka.concordiapi.Tasks.Updaters.UpdateDateUpdater;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskCREATEUpdaterBasic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskUpdateDateCREATEUpdater implements TaskCREATEUpdaterBasic {

    private final TaskUpdateDateUpdaterHelper taskUpdateDateUpdaterHelper;

    @Autowired
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
