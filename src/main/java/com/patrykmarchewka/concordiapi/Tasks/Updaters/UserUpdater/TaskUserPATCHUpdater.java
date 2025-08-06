package com.patrykmarchewka.concordiapi.Tasks.Updaters.UserUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPATCHUpdater;

public class TaskUserPATCHUpdater implements TaskPATCHUpdater {

    private final TaskUserUpdaterHelper taskUserUpdaterHelper;


    public TaskUserPATCHUpdater(TaskUserUpdaterHelper taskUserUpdaterHelper) {
        this.taskUserUpdaterHelper = taskUserUpdaterHelper;
    }

    /**
     * Sets the users assigned to ask if present in body
     *
     * @param task Task to edit
     * @param body TaskRequestBody with data
     */
    @Override
    public void PATCHUpdate(Task task, TaskRequestBody body) {
        if (body.getUsers() != null) {
            taskUserUpdaterHelper.sharedUpdate(task, body.getUsers());
        }
    }
}
