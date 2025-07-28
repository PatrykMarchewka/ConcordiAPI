package com.patrykmarchewka.concordiapi.Tasks.Updaters.UserUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskCREATEUpdater;
import com.patrykmarchewka.concordiapi.UpdateType;

/**
 * Handles updating the {@code users} field in {@link Task} entity
 */
public class TaskUserCREATEUpdater implements TaskCREATEUpdater {

    private final TaskUserUpdaterHelper taskUserUpdaterHelper;

    public TaskUserCREATEUpdater(TaskUserUpdaterHelper taskUserUpdaterHelper){
        this.taskUserUpdaterHelper = taskUserUpdaterHelper;
    }


    @Override
    public boolean supports(UpdateType updateType) {
        return updateType == UpdateType.CREATE;
    }

    /**
     * Sets the users assigned to the task
     *
     * @param task Task to create
     * @param body TaskRequestBody containing user ID values
     */
    @Override
    public void CREATEUpdate(Task task, TaskRequestBody body) {
        taskUserUpdaterHelper.sharedUpdate(task, body.getUsers());
    }

}