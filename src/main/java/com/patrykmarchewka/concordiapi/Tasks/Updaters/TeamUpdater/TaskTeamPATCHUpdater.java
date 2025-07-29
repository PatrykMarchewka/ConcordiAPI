package com.patrykmarchewka.concordiapi.Tasks.Updaters.TeamUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskPATCHUpdater;
import java.util.function.Supplier;


public class TaskTeamPATCHUpdater implements TaskPATCHUpdater {

    private final TaskTeamUpdaterHelper taskTeamUpdaterHelper;
    private final Supplier<Team> team;

    public TaskTeamPATCHUpdater(TaskTeamUpdaterHelper taskTeamUpdaterHelper, Supplier<Team> team) {
        this.taskTeamUpdaterHelper = taskTeamUpdaterHelper;
        this.team = team;
    }


    @Override
    public void PATCHUpdate(Task task, TaskRequestBody body) {
        taskTeamUpdaterHelper.removeTeamIfAssigned(task, team);
        taskTeamUpdaterHelper.sharedUpdate(task,body);
    }
}
