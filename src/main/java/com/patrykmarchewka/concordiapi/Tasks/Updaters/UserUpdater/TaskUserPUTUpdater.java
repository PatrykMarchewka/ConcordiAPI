package com.patrykmarchewka.concordiapi.Tasks.Updaters.UserUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPUTUpdater;

public class TaskUserPUTUpdater implements TaskPUTUpdater {

    private final TaskUserUpdaterHelper taskUserUpdaterHelper;

    public TaskUserPUTUpdater(TaskUserUpdaterHelper taskUserUpdaterHelper) {
        this.taskUserUpdaterHelper = taskUserUpdaterHelper;
    }

    /**
     * Sets the users assigned to task to new value from the body
     *
     * @param task Task to edit
     * @param body TaskRequestBody containing new values
     */
    @Override
    public void PUTUpdate(Task task, TaskRequestBody body) {
        taskUserUpdaterHelper.sharedUpdate(task, body.getUsers());
    }

}