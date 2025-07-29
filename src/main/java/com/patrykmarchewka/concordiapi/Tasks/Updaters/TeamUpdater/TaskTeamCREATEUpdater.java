package com.patrykmarchewka.concordiapi.Tasks.Updaters.TeamUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskCREATEUpdater;


public class TaskTeamCREATEUpdater implements TaskCREATEUpdater {

    private final TaskTeamUpdaterHelper taskTeamUpdaterHelper;

    public TaskTeamCREATEUpdater(TaskTeamUpdaterHelper taskTeamUpdaterHelper) {
        this.taskTeamUpdaterHelper = taskTeamUpdaterHelper;
    }

    @Override
    public void CREATEUpdate(Task task, TaskRequestBody body) {
        taskTeamUpdaterHelper.sharedUpdate(task, body);
    }
}
