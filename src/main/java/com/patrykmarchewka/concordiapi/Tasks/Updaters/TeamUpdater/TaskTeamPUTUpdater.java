package com.patrykmarchewka.concordiapi.Tasks.Updaters.TeamUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPUTUpdater;

import java.util.function.Supplier;

public class TaskTeamPUTUpdater implements TaskPUTUpdater {

    private final TaskTeamUpdaterHelper taskTeamUpdaterHelper;
    private final Supplier<Team> team;

    public TaskTeamPUTUpdater(Supplier<Team> team, TaskTeamUpdaterHelper taskTeamUpdaterHelper) {
        this.team = team;
        this.taskTeamUpdaterHelper = taskTeamUpdaterHelper;
    }

    @Override
    public void PUTUpdate(Task task, TaskRequestBody body) {
        taskTeamUpdaterHelper.removeTeamIfAssigned(task, team);
        taskTeamUpdaterHelper.sharedUpdate(task, body);
    }


}
